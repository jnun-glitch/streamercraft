package dev.streamercraft.client.paytest;

import dev.streamercraft.StreamerCraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Captures visible messages for a short, explicitly user-triggered payment test. */
public final class PayTestCapture {
    private static final Duration CAPTURE_WINDOW = Duration.ofSeconds(30);
    private static final int MAX_QUEUED_MESSAGES = 256;
    private static final int MAX_MESSAGE_LENGTH = 4096;
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter
            .ofPattern("yyyyMMdd-HHmmss-SSS")
            .withZone(ZoneId.systemDefault());

    private static volatile Session activeSession;

    private PayTestCapture() {
    }

    public static void registerLifecycle() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> stop("client_stopping", true));
    }

    /** Called for the client's outgoing command hook. The command is never modified or canceled. */
    public static void onCommandSent(String command) {
        String normalized = command.strip().replaceAll("\\s+", " ");
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!"pay test".equalsIgnoreCase(normalized)) {
            return;
        }

        Session previous = activeSession;
        if (previous != null) {
            activeSession = null;
            previous.finish("restarted");
        }

        Instant startedAt = Instant.now();
        long deadlineNanos = System.nanoTime() + CAPTURE_WINDOW.toNanos();
        Path outputFile = createOutputPath(startedAt);
        Session session = new Session("/" + command.strip(), startedAt, deadlineNanos, outputFile);
        activeSession = session;
        session.startWriter();

        tellPlayer("streamercraft.paytest.started");
        StreamerCraft.LOGGER.info("Payment-message capture started for {} seconds. File: {}",
                CAPTURE_WINDOW.toSeconds(), outputFile);
    }

    public static void onServerMessage(Component message, boolean overlay) {
        if (activeSession == null) {
            return;
        }
        String text = message.getString();
        if (PaymentMessageParser.isPlayerChat(text)) {
            return;
        }
        PaymentMessageParser.PaymentNotice notice = PaymentMessageParser.parseServerMessage(text).orElse(null);
        Session session = record("SERVER_MESSAGE", overlay, text, notice);
        if (notice != null && session != null && activeSession == session) {
            activeSession = null;
            session.finish("payment_message_pattern_detected");
            tellPaymentDetected(notice);
            StreamerCraft.LOGGER.info("A Donut payment-message pattern was captured in {}.", session.outputFile());
        }
    }

    public static void tick() {
        Session session = activeSession;
        if (session != null && session.isExpired(System.nanoTime())) {
            activeSession = null;
            session.finish("timeout");
            tellPlayer("streamercraft.paytest.finished");
            StreamerCraft.LOGGER.info("Payment-message capture window ended. File: {}", session.outputFile());
        }
    }

    private static Session record(String type, boolean overlay, String message,
                                  PaymentMessageParser.PaymentNotice paymentNotice) {
        Session session = activeSession;
        if (session == null || session.isExpired(System.nanoTime())) {
            return null;
        }

        String boundedMessage = message.length() > MAX_MESSAGE_LENGTH
                ? message.substring(0, MAX_MESSAGE_LENGTH) + "… [truncated]"
                : message;
        String classification = paymentNotice != null ? "PAYMENT_MESSAGE_MATCH" : "UNCLASSIFIED";
        String line = "[" + Instant.now() + "] type=" + type
                + " classification=" + classification
                + " overlay=" + overlay
                + " message=\"" + escape(boundedMessage) + "\"";
        if (paymentNotice == null) {
            session.offer(line);
        } else {
            line += " payment_sender=\"" + escape(paymentNotice.sender()) + "\""
                    + " payment_amount=\"" + escape(paymentNotice.amount()) + "\""
                    + " pattern=\"donut_" + paymentNotice.language() + "\"";
            session.setPaymentCandidateLine(line);
        }
        return session;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static Path createOutputPath(Instant startedAt) {
        return FabricLoader.getInstance().getConfigDir()
                .resolve(StreamerCraft.MOD_ID)
                .resolve("pay-tests")
                .resolve("pay-test-" + FILE_TIMESTAMP.format(startedAt) + "-" + UUID.randomUUID() + ".log");
    }

    private static void tellPlayer(String translationKey) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.displayClientMessage(Component.translatable(translationKey), false);
        }
    }

    private static void tellPaymentDetected(PaymentMessageParser.PaymentNotice notice) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.displayClientMessage(Component.translatable(
                    "streamercraft.paytest.payment_detected",
                    Component.literal(notice.sender()),
                    Component.literal(notice.amount())
            ), false);
        }
    }

    private static void stop(String reason, boolean waitForWriter) {
        Session session = activeSession;
        activeSession = null;
        if (session != null) {
            session.finish(reason);
            if (waitForWriter) {
                session.awaitWriter();
            }
        }
    }

    private static final class Session {
        private final String triggerCommand;
        private final Instant startedAt;
        private final long deadlineNanos;
        private final Path outputFile;
        private final ArrayBlockingQueue<String> pendingMessages = new ArrayBlockingQueue<>(MAX_QUEUED_MESSAGES);
        private final AtomicBoolean finishRequested = new AtomicBoolean();
        private final AtomicInteger savedMessages = new AtomicInteger();
        private final AtomicInteger droppedMessages = new AtomicInteger();
        private final Thread writerThread;
        private volatile String finishReason = "timeout";
        private volatile String paymentCandidateLine;
        private volatile boolean writerFailed;

        private Session(String triggerCommand, Instant startedAt, long deadlineNanos, Path outputFile) {
            this.triggerCommand = triggerCommand;
            this.startedAt = startedAt;
            this.deadlineNanos = deadlineNanos;
            this.outputFile = outputFile;
            this.writerThread = new Thread(this::writeFile, "StreamerCraft-PayTestWriter");
            this.writerThread.setDaemon(true);
        }

        private void startWriter() {
            writerThread.start();
        }

        private boolean isExpired(long nowNanos) {
            return nowNanos - deadlineNanos >= 0;
        }

        private Path outputFile() {
            return outputFile;
        }

        private void offer(String line) {
            if (writerFailed || !pendingMessages.offer(line)) {
                droppedMessages.incrementAndGet();
            }
        }

        private void finish(String reason) {
            finishReason = reason;
            finishRequested.set(true);
        }

        private void setPaymentCandidateLine(String line) {
            paymentCandidateLine = line;
        }

        private void awaitWriter() {
            try {
                writerThread.join(TimeUnit.SECONDS.toMillis(2));
                if (writerThread.isAlive()) {
                    StreamerCraft.LOGGER.warn("Payment-test log writer is still finishing: {}", outputFile);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                StreamerCraft.LOGGER.warn("Interrupted while waiting for payment-test log writer.", exception);
            }
        }

        private void writeFile() {
            try {
                Files.createDirectories(outputFile.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                    writeHeader(writer);
                    while (!finishRequested.get() || !pendingMessages.isEmpty()) {
                        String line = pendingMessages.poll(100, TimeUnit.MILLISECONDS);
                        if (line != null) {
                            writer.write(line);
                            writer.newLine();
                            writer.flush();
                            savedMessages.incrementAndGet();
                        }
                    }
                    writer.newLine();
                    if (paymentCandidateLine == null) {
                        writer.write("Payment pattern match: none.");
                    } else {
                        writer.write("Payment pattern match (text only; not server-confirmed):");
                        writer.newLine();
                        writer.write(paymentCandidateLine);
                        savedMessages.incrementAndGet();
                    }
                    writer.newLine();
                    writer.write("Finished: " + Instant.now());
                    writer.newLine();
                    writer.write("Finish reason: " + finishReason);
                    writer.newLine();
                    writer.write("Messages saved: " + savedMessages.get());
                    writer.newLine();
                    writer.write("Messages dropped because the bounded queue was full: " + droppedMessages.get());
                    writer.newLine();
                    writer.flush();
                }
                StreamerCraft.LOGGER.info("Payment-test capture saved: {} ({} messages, {} dropped)",
                        outputFile, savedMessages.get(), droppedMessages.get());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                writerFailed = true;
                StreamerCraft.LOGGER.error("Payment-test log writer was interrupted: {}", outputFile, exception);
            } catch (IOException | RuntimeException exception) {
                writerFailed = true;
                StreamerCraft.LOGGER.error("Could not write payment-test capture to {}.", outputFile, exception);
            }
        }

        private void writeHeader(BufferedWriter writer) throws IOException {
            writer.write("StreamerCraft payment-message diagnostic capture");
            writer.newLine();
            writer.write("Status: Donut-format text patterns are matched as candidates, not server-confirmed transactions.");
            writer.newLine();
            writer.write("Started: " + startedAt);
            writer.newLine();
            writer.write("Capture window: " + CAPTURE_WINDOW.toSeconds() + " seconds");
            writer.newLine();
            writer.write("Trigger command: " + triggerCommand);
            writer.newLine();
            writer.write("Scope: visible server-origin messages received by this client only; player chat is ignored.");
            writer.newLine();
            writer.write("Storage: local Minecraft config folder; no diagnostic data is uploaded.");
            writer.newLine();
            writer.write("The manually typed trigger command is sent to the current server unchanged.");
            writer.newLine();
            writer.newLine();
            writer.write("Messages:");
            writer.newLine();
            writer.flush();
        }
    }
}

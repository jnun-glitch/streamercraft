package dev.streamercraft.client.paytest;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Matches the two Donut payment notices supplied by the user. */
public final class PaymentMessageParser {
    private static final Pattern DONUT_GERMAN_PAYMENT = Pattern.compile(
            "^(?<sender>.+?)\\s+hat dir\\s+(?<amount>\\$\\s*\\d[\\d.,]*(?:\\s*[KMBT])?)\\s+gezahlt\\.?$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    private static final Pattern DONUT_ENGLISH_PAYMENT = Pattern.compile(
            "^You received\\s+(?<amount>\\$\\s*\\d[\\d.,]*(?:\\s*[KMBT])?)\\s+from\\s+(?<sender>.+?)\\.?$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    private static final Pattern PLAYER_CHAT_PREFIX = Pattern.compile("^<PLAYER\\b", Pattern.CASE_INSENSITIVE);

    private PaymentMessageParser() {
    }

    /**
     * Parses server-origin messages only. Player chat is excluded by the caller and by the visible prefix guard.
     * The result is a text-pattern match, not proof of a server-side transaction.
     */
    public static Optional<PaymentNotice> parseServerMessage(String message) {
        String candidate = message.stripLeading();
        if (isPlayerChat(candidate)) {
            return Optional.empty();
        }

        Matcher german = DONUT_GERMAN_PAYMENT.matcher(candidate);
        if (german.matches()) {
            return notice(german, "de");
        }

        Matcher english = DONUT_ENGLISH_PAYMENT.matcher(candidate);
        if (english.matches()) {
            return notice(english, "en");
        }

        return Optional.empty();
    }

    /** Donut log lines with a leading player tag are player messages, not server notices. */
    public static boolean isPlayerChat(String message) {
        return PLAYER_CHAT_PREFIX.matcher(message.stripLeading()).find();
    }

    private static Optional<PaymentNotice> notice(Matcher matcher, String language) {
        String sender = matcher.group("sender").strip();
        String amount = matcher.group("amount").strip().replaceAll("\\s+", "");
        if (sender.isEmpty() || amount.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new PaymentNotice(sender, amount, language));
    }

    public record PaymentNotice(String sender, String amount, String language) {
    }
}

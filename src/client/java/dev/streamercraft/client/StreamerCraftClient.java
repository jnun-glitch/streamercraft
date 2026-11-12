package dev.streamercraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.streamercraft.StreamerCraft;
import dev.streamercraft.client.paytest.PayTestCapture;
import dev.streamercraft.client.screen.DashboardScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class StreamerCraftClient implements ClientModInitializer {
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(StreamerCraft.MOD_ID, "controls")
    );

    private static final KeyMapping OPEN_DASHBOARD = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.streamercraft.open_dashboard",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_O,
                    KEY_CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_DASHBOARD.consumeClick()) {
                client.setScreen(new DashboardScreen(client.screen));
            }
            PayTestCapture.tick();
        });

        ClientSendMessageEvents.COMMAND.register(PayTestCapture::onCommandSent);
        ClientReceiveMessageEvents.GAME.register(PayTestCapture::onServerMessage);
        PayTestCapture.registerLifecycle();

        StreamerCraft.LOGGER.info("StreamerCraft client controls registered.");
    }
}

package dev.streamercraft.client.screen;

import dev.streamercraft.StreamerCraft;
import dev.streamercraft.config.SettingsConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DashboardScreen extends Screen {
    private final Screen parent;

    public DashboardScreen(Screen parent) {
        super(Component.translatable("screen.streamercraft.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(220, Math.max(120, width - 32));
        int buttonX = (width - buttonWidth) / 2;
        int buttonY = Math.min(height - 52, height / 2 + 72);

        addRenderableWidget(Button.builder(
                Component.translatable("screen.streamercraft.theme.toggle"),
                button -> StreamerCraft.settings().toggleTheme()
        ).bounds(buttonX, buttonY, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> onClose()
        ).bounds(buttonX, buttonY + 26, buttonWidth, 20).build());
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int panelWidth = Math.min(460, width - 32);
        int panelLeft = (width - panelWidth) / 2;
        int panelTop = Math.max(24, height / 2 - 112);
        int panelBottom = Math.min(height - 74, panelTop + 150);

        boolean midnight = SettingsConfig.MIDNIGHT_THEME.equals(StreamerCraft.settings().themeId());
        int panelColor = midnight ? 0xF20B101B : 0xF2131730;
        int accentColor = midnight ? 0xFF66B9FF : 0xFFB899FF;

        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelBottom, panelColor);
        graphics.renderOutline(panelLeft, panelTop, panelWidth, panelBottom - panelTop, accentColor);
        graphics.fill(panelLeft + 1, panelTop + 1, panelLeft + panelWidth - 1, panelTop + 3, accentColor);

        graphics.drawCenteredString(font,
                Component.translatable("screen.streamercraft.title"),
                width / 2, panelTop + 15, 0xFFFFFFFF);
        graphics.drawCenteredString(font,
                Component.translatable("screen.streamercraft.subtitle"),
                width / 2, panelTop + 32, 0xFFCFD5E8);
        graphics.drawString(font,
                Component.translatable("screen.streamercraft.profile.none"),
                panelLeft + 18, panelTop + 61, 0xFFE7EAF4, false);
        graphics.drawString(font,
                Component.translatable("screen.streamercraft.data.none"),
                panelLeft + 18, panelTop + 78, 0xFFB6C0D8, false);
        graphics.drawString(font,
                Component.translatable("screen.streamercraft.theme.current",
                        Component.translatable("screen.streamercraft.theme." + StreamerCraft.settings().themeId())),
                panelLeft + 18, panelTop + 105, accentColor, false);
    }
}

package dev.streamercraft.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.streamercraft.client.screen.DashboardScreen;

public final class StreamerCraftModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DashboardScreen::new;
    }
}

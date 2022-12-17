package com.johanvonelectrum.yaum;

import com.johanvonelectrum.yaum.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;

public class Yaum implements ModInitializer {
    @Override
    public void onInitialize() {
        SettingsManager.init();
        System.out.println(SettingsManager.get("requireClient").toString());
    }
}

package com.johanvonelectrum.johanutils;

import com.johanvonelectrum.johanutils.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;

public class JohanUtils implements ModInitializer {
    @Override
    public void onInitialize() {
        SettingsManager.init();
        System.out.println(SettingsManager.get("requireClient").toString());
    }
}

package com.johanvonelectrum.yaum;

import com.johanvonelectrum.yaum.lang.Language;
import com.johanvonelectrum.yaum.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import com.johanvonelectrum.yaum.commands.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Yaum implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        loadLanguages();
        SettingsManager.init();

        CommandRegistrationCallback.EVENT.register(Yaum::registerCommands);
    }

    private static void loadLanguages() {
        Language.load("en_us");
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LOGGER.info("Registering commands...");
        YaumCommand.register(dispatcher, dedicated);
    }
}

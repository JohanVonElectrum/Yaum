package com.johanvonelectrum.yaum;

import com.johanvonelectrum.yaum.commands.CommandBatch;
import com.johanvonelectrum.yaum.commands.CommandComputation;
import com.johanvonelectrum.yaum.commands.YaumCommand;
import com.johanvonelectrum.yaum.lang.Language;
import com.johanvonelectrum.yaum.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
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
        Language.load(YaumSettings.defaultLanguage);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LOGGER.info("Registering commands...");
        YaumCommand.register(dispatcher, dedicated);
        CommandBatch.register(dispatcher);
        CommandComputation.register(dispatcher);
    }

    public static void onServerLoaded(MinecraftServer minecraftServer) {
        SettingsManager.attach(minecraftServer);
    }

    public static void onServerUnloaded() {
        SettingsManager.detach();
    }
}

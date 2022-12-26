package com.johanvonelectrum.yaum.settings;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.johanvonelectrum.yaum.YaumSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

    public static Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private static final Map<String, ParsedRule<?>> rules = new HashMap<>();

    private static MinecraftServer minecraftServer;

    /**
     * Load level rules.
     */
    public static void attach(MinecraftServer minecraftServer) {
        SettingsManager.minecraftServer = minecraftServer;
        Path levelPath = minecraftServer.getSavePath(WorldSavePath.ROOT);
        File file = levelPath.resolve("yaum.json").toFile();
        LOGGER.info("Loading level \"{}\" rules...", levelPath.getFileName().toString());
        try {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> rules = GSON.fromJson(new FileReader(file), type);
            for (Map.Entry<String, String> ruleEntry: rules.entrySet()) {
                ParsedRule<?> rule = SettingsManager.get(ruleEntry.getKey());
                if (rule == null) {
                    LOGGER.warn("{} is not an existing Yaum rule.", ruleEntry.getKey());
                    continue;
                }
                try {
                    rule.castAndSet(minecraftServer.getCommandSource(), ruleEntry.getValue(), true);
                } catch (InvalidRuleValueException e) {
                    LOGGER.error("Value {} is not valid for rule {}: {}", ruleEntry.getValue(), ruleEntry.getKey(), e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.info("Generating yaum.json for level {}...", levelPath.getFileName().toString());
            try {
                Map<String, String> rules = new HashMap<>();
                for (Map.Entry<String, ParsedRule<?>> rule: SettingsManager.rules().entrySet()) {
                    rules.put(rule.getKey(), rule.getValue().originalValue().toString());
                }
                Writer writer = new FileWriter(file);
                GSON.toJson(rules, writer);
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void detach() {
        for (ParsedRule<?> rule: rules.values()) {
            rule.reset(minecraftServer.getCommandSource(), true);
        }
    }

    /**
     * Parse and load rules.
     */
    public static void init() {
        parse(YaumSettings.class);
        load();
    }

    /**
     * Load default settings from file.
     * TODO: client/server rules.
     */
    public static void load() {

    }

    /**
     * Parse settings from source.
     */
    public static void parse(Class<?> settingsClass) {
        for (Field field : settingsClass.getDeclaredFields()) {
            Rule rule = field.getAnnotation(Rule.class);
            if (rule == null) continue;

            ParsedRule<?> parsed = ParsedRuleImpl.of(field, rule);
            rules.put(parsed.name(), parsed);
        }
    }

    /**
     * Save default settings to a file.
     */
    public static void save() {

    }

    /**
     * Get a ParsedRule by name.
     */
    public static ParsedRule<?> get(String name) {
        return rules.get(name);
    }

    public static Map<String, ParsedRule<?>> rules() {
        return rules;
    }
}

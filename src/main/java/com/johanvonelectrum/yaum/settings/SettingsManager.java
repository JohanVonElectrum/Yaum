package com.johanvonelectrum.yaum.settings;

import com.johanvonelectrum.yaum.YaumSettings;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

    private static final Map<String, ParsedRule<?>> rules = new HashMap<>();

    /**
     * Parse and load rules.
     */
    public static void init() {
        parse(YaumSettings.class);
        load();
    }

    /**
     * Load default settings from file.
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
}

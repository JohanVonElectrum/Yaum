package com.johanvonelectrum.yaum.lang;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    private static final Map<String, Language> languages = new HashMap<>();
    private final Map<String, String> translations;

    public Language(Map<String, String> translations) {
        this.translations = translations;
    }

    public static void load(String lang) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        BiConsumer<String, String> biConsumer = builder::put;
        final String file = "/assets/yaum/lang/" + lang + ".json";
        try (InputStream inputStream = Language.class.getResourceAsStream(file)) {
            JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String string = TOKEN_PATTERN.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                biConsumer.accept(entry.getKey(), string);
            }
            languages.put(lang, new Language(builder.build()));
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't read strings from {}", file, exception);
        }
    }

    public boolean hasTranslation(String key) {
        return translations.containsKey(key);
    }

    public String translate(String key, Object... args) {
        return String.format(translations.get(key), args);
    }

    public static boolean hasTranslation(String lang, String key) {
        return languages.containsKey(lang) && languages.get(lang).hasTranslation(key);
    }

    public static String translate(String lang, String key, Object... args) {
        return languages.get(lang).translate(key, args);
    }

    public static Optional<String> safeTranslate(String lang, String key, Object... args) {
        if (!hasTranslation(lang, key)) {
            LOGGER.error("Couldn't find any valid translation for {} in {}", key, lang);
            return Optional.empty();
        }

        return Optional.of(translate(lang, key, args));
    }

    public static String tryTranslate(String lang, String key, Object... args) {
        if (!hasTranslation(lang, key)) {
            return key;
        }

        return translate(lang, key, args);
    }

}

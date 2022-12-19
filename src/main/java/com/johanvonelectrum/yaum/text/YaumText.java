package com.johanvonelectrum.yaum.text;

import net.minecraft.text.Text;

public interface YaumText {

    static YaumLiteralText literal(String text) {
        return new YaumLiteralText(text);
    }

    static YaumTranslatableText translatable(String key, Object... args) {
        return new YaumTranslatableText(key, args);
    }

    Text asText();

    default String asString() {
        return this.asText().asString();
    }

}

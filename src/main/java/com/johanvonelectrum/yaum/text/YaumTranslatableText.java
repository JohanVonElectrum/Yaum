package com.johanvonelectrum.yaum.text;

import com.johanvonelectrum.yaum.lang.Language;
import net.minecraft.text.Text;

import java.util.Optional;

public class YaumTranslatableText implements YaumText {

    private final String key;
    private final Object[] args;

    public YaumTranslatableText(String key) {
        this.key = key;
        this.args = null;
    }

    public YaumTranslatableText(String key, Object... args) {
        this.key = key;
        this.args = args;
    }

    @Override
    public Text asText() {
        return asText("en_us");
    }

    public Text asText(String lang) {
        return YaumText.literal(String.format(Language.tryTranslate(lang, this.key), this.args)).asText();
    }
}

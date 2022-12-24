package com.johanvonelectrum.yaum.text;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.lang.Language;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

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
        return asText(YaumSettings.defaultLanguage);
    }

    public Text asText(String lang) {
        return YaumText.literal(Language.tryTranslate(lang, this.key, this.args)).asText();
    }
}

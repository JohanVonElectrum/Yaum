package com.johanvonelectrum.yaum.test;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.lang.Language;
import com.johanvonelectrum.yaum.text.YaumText;
import com.johanvonelectrum.yaum.text.format.YaumFormatter;

public class YaumFormatterTest {

    public static void main(String[] args) {
        assert YaumText.literal("normal\\**italic\\*italic*").asText().toString().equals(
                "TextComponent{text='normal*', siblings=[TextComponent{text='italic*italic', siblings=[], " +
                        "style=Style{ color=null, bold=null, italic=true, underlined=null, strikethrough=null, " +
                        "obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, " +
                        "font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, " +
                        "underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}");

        assert YaumText.literal("**bold __underline__**").asText().toString().equals(
                "TextComponent{text='bold ', siblings=[TextComponent{text='underline', siblings=[], " +
                        "style=Style{ color=null, bold=true, italic=null, underlined=true, strikethrough=null, " +
                        "obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, " +
                        "font=minecraft:default}}], style=Style{ color=null, bold=true, italic=null, " +
                        "underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}"
        );

        assert YaumText.literal("**bold** normal **bold**").asText().toString().equals(
                "TextComponent{text='bold', siblings=[TextComponent{text=' normal ', siblings=[], style=Style{ " +
                        "color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, " +
                        "TextComponent{text='bold', siblings=[], style=Style{ color=null, bold=true, italic=null, " +
                        "underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=true, italic=null, " +
                        "underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}"
        );

        assert YaumText.literal("[true](/yaum rule test true)").asText().toString().equals(
                "TextComponent{text='true', siblings=[], style=Style{ color=null, bold=null, italic=null, " +
                        "underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=ClickEvent{action=RUN_COMMAND, value='/yaum rule test true'}, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}");

        Language.load("en_us");
        assert YaumText.translatable(
                "error.yaum.invalid-rule-value",
                "defaultLanguage", Language.tryTranslate(
                        YaumSettings.defaultLanguage,
                        "error.yaum.invalid-rule-value.validator",
                        "1", "language", Language.tryTranslate(
                                YaumSettings.defaultLanguage,"validator.yaum.language.description", "1"
                        )
                )
        ).asText().toString().equals(
                "TextComponent{text='Couldn't set value for rule ', siblings=[TextComponent{text='defaultLanguage', " +
                        "siblings=[], style=Style{ color=null, bold=true, italic=null, underlined=null, " +
                        "strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, " +
                        "font=minecraft:default}}, TextComponent{text=': The value ', siblings=[], style=Style{ " +
                        "color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, " +
                        "TextComponent{text='1', siblings=[], style=Style{ color=null, bold=null, italic=true, " +
                        "underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, " +
                        "insertion=null, font=minecraft:default}}, TextComponent{text=' is not accepted by the " +
                        "validator ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, " +
                        "strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, " +
                        "font=minecraft:default}}, TextComponent{text='language', siblings=[], style=Style{ " +
                        "color=null, bold=true, italic=null, underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, " +
                        "TextComponent{text=': Language ', siblings=[], style=Style{ color=null, bold=null, " +
                        "italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, " +
                        "hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='1', " +
                        "siblings=[], style=Style{ color=null, bold=null, italic=true, underlined=null, " +
                        "strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, " +
                        "font=minecraft:default}}, TextComponent{text=' is not loaded.', siblings=[], style=Style{ " +
                        "color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ " +
                        "color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, " +
                        "clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}");

        System.out.println(YaumText.translatable("command.yaum.yaum.set", YaumFormatter.escape("defaultLanguage"), YaumFormatter.escape("en_us")).asText().toString());
    }

}

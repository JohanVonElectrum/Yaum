package com.johanvonelectrum.yaum.test;

import com.johanvonelectrum.yaum.text.YaumText;

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
    }

}

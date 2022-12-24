package com.johanvonelectrum.yaum.text.format;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.util.Map;
import java.util.function.Function;

public class Token {

    private static final Map<String, Function<Style, Style>> STYLERS = Map.ofEntries(
            Map.entry("italic", style -> style.withItalic(true)),
            Map.entry("bold", style -> style.withBold(true)),
            Map.entry("underline", style -> style.withUnderline(true)),
            Map.entry("strikethrough", style -> style.withStrikethrough(true)),
            Map.entry("obfuscate", style -> style.obfuscated(true))
    );

    private final String content;
    private final String rawContent;
    private Style style;
    private final String rawStyle;

    private Token(String content, String style, Style inherit) {
        this.content = content
                .replaceAll("\\\\\\*", "*")
                .replaceAll("\\\\_", "*")
                .replaceAll("\\\\\\[", "[")
                .replaceAll("\\\\]", "]");
        this.rawContent = content;

        this.style = inherit;
        for (String feature: style.split(",")) {
            if (feature.equals("normal")) continue;
            this.style = STYLERS.get(feature).apply(this.style);
        }
        this.rawStyle = style;
    }

    public static Token of(String content, String style, Style inherit) {
        if (content == null) throw new IllegalArgumentException("Can't create a Token with null content.");
        return new Token(content, style, inherit);
    }

    public static Token of(String content, String style) {
        if (content == null) throw new IllegalArgumentException("Can't create a Token with null content.");
        return of(content, style, Style.EMPTY);
    }

    public Token merge(Token inner) {
        return Token.of(inner.content, rawStyle + "," + inner.rawStyle, style);
    }

    public Style getStyle() {
        return style;
    }

    public MutableText asText() {
        return new LiteralText(this.content).setStyle(this.getStyle());
    }

    public Token onClick(String action) {
        if (action.startsWith("/"))
            this.style = this.style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
        else if (action.startsWith("*/"))
            this.style = this.style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, action));
        else if (action.startsWith("http"))
            this.style = this.style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, action));

        return this;
    }

    public String getContent() {
        return content;
    }

    public String getRawContent() {
        return rawContent;
    }
}

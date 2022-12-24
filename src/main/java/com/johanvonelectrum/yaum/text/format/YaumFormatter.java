package com.johanvonelectrum.yaum.text.format;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

/**
 * YaumFormatter is a text formatter which can generate {@link Token} streams out of raw text.
 * <p>
 * Default formats are {@code *italic*}, {@code _italic_}, {@code **bold**}, {@code __underline__},
 * {@code -strikethrough-}, {@code ~obfuscate~}.
 * </p>
 * <p>
 *     Use {@code _**bold italic**_} or {@code **_bold italic_**} instead of {@code ***bold italic***}.
 *     Use {@code __*underline italic*__} or {@code *__underline italic__*} instead of {@code ___underline italic___}.
 * </p>
 *
 * @see Token
 * @see Format
 *
 * @version 1.0.1
 * @author JohanVonElectrum
 */
public class YaumFormatter {

    private static final Format[] FORMATS = {
            Format.of("\\*", s -> Token.of(s, "italic"), 1),
            Format.of("\\*\\*", s -> Token.of(s, "bold"), 0),
            Format.of("_", s -> Token.of(s, "italic"), 1),
            Format.of("__", s -> Token.of(s, "underline"), 0),
            Format.of("-", s -> Token.of(s, "strikethrough"), 0),
            Format.of("~", s -> Token.of(s, "obfuscate"), 0),
            Format.of(Pattern.compile("\\[(.+?[^\\\\])]\\((.+?[^\\\\])\\)"), matcher -> Token.of(matcher.group(1), "normal").onClick(matcher.group(2)), 0),
    };

    private String content;

    /**
     * Create a new {@link YaumFormatter} for parsing the content.
     * @param content String which will be parsed during this object life cycle.
     */
    public YaumFormatter(String content) {
        this.content = content;
    }

    /**
     * Escape the string passed as parameter.
     *
     * @param string String to be escaped.
     *
     * @return Escaped string based on input.
     * @since 1.0.1
     */
    public static String escape(String string) {
        return string
                .replaceAll("\\*", "\\\\*")
                .replaceAll("_", "\\\\_")
                .replaceAll("-", "\\\\-")
                .replaceAll("~", "\\\\~");
    }

    public Text parse() { //TODO: Return YaumText when YaumText extends Text
        Optional<MutableText> texts = tokenize().stream().map(Token::asText).reduce(MutableText::append);

        return texts.orElse(null);
    }

    /**
     * Tokenizes the content set in constructor.
     *
     * @return Ordered {@link List<Token>}<{@link Token}> segmented by styles to apply.
     * @since 1.0.0
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (!this.content.isEmpty())
            tokens.add(next());

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getStyle().isEmpty()) continue;

            tokens.remove(i);
            List<Token> inner = new YaumFormatter(token.getRawContent()).tokenize();
            tokens.addAll(i, inner.stream().map(token::merge).toList());
            i += inner.size() - 1;
        }

        return tokens;
    }

    /**
     * Get the next token to process and remove it from the content.
     *
     * @return The next token to process.
     * @since 1.0.0
     */
    private Token next() {
        Token nextToken = null;
        int minIndex = this.content.length();
        int len = 0;
        for (Format format: Arrays.stream(FORMATS).sorted(Comparator.comparingInt(Format::priority)).toList()) {
            int nextIndex = format.start(this.content);
            if (nextIndex == -1) continue;
            if (nextIndex >= minIndex) continue;
            if (nextIndex > 0 && this.content.charAt(nextIndex - 1) == '\\') {
                minIndex = nextIndex + 1;
                continue;
            }
            if ((nextToken = format.apply(this.content.substring(nextIndex, nextIndex + format.length(this.content, nextIndex)))) == null) continue;
            minIndex = nextIndex;
            len = format.length(this.content, nextIndex);
        }

        if (minIndex > 0 || nextToken == null) {
            nextToken = Token.of(this.content.substring(0, minIndex), "normal");
            len = nextToken.getRawContent().length();
        }

        this.content = this.content.substring(len);
        return nextToken;
    }

}

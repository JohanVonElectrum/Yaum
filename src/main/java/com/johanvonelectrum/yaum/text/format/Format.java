package com.johanvonelectrum.yaum.text.format;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Format(Pattern pattern, Formatter formatter, int priority, boolean custom) {

    @FunctionalInterface
    interface Formatter {
        Token apply(Matcher matcher);
    }

    public static Format of(Pattern pattern, Formatter formatter, int priority, boolean custom) {
        return new Format(pattern, formatter, priority, custom);
    }

    public static Format of(Pattern pattern, Formatter formatter, int priority) {
        return of(pattern, formatter, priority, true);
    }

    public static Format of(String start, String end, String content, Formatter formatter, int priority) {
        return of(Pattern.compile(start + "(" + content + "?[^\\\\])" + end), formatter, priority, false);
    }

    public static Format of(String start, String end, Formatter formatter, int priority) {
        return of(start, end, ".+", formatter, priority);
    }

    public static Format of(String start, Function<String, Token> formatter, int priority) {
        return of(start, start, matcher -> formatter.apply(matcher.group(1)), priority);
    }

    public int start(String string) {
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) return -1;
        return matcher.start();
    }

    public int length(String string, int index) {
        Matcher matcher = pattern.matcher(string.substring(index));
        if (!matcher.find()) return -1;
        return matcher.end();
    }

    public Token apply(String string) {
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) return null;
        return this.formatter.apply(matcher);
    }
}

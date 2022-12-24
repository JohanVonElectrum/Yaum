package com.johanvonelectrum.yaum.settings;

import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface FromStringConverter<T> {
    T convert(String value) throws InvalidRuleValueException;

    Map<Class<?>, FromStringConverter<?>> CONVERTER_MAP = Map.ofEntries(
            Map.entry(String.class, (FromStringConverter<String>) value -> value),
            Map.entry(Boolean.class, (FromStringConverter<Boolean>) value -> switch (value) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new InvalidRuleValueException("Invalid boolean value");
            }),
            numericalConverter(Integer.class, Integer::parseInt),
            numericalConverter(Double.class, Double::parseDouble),
            numericalConverter(Long.class, Long::parseLong),
            numericalConverter(Float.class, Float::parseFloat)
    );

    private static <T> Map.Entry<Class<T>, FromStringConverter<T>> numericalConverter(Class<T> outputClass, Function<String, T> converter) {
        return Map.entry(outputClass, value -> {
            try {
                return converter.apply(value);
            } catch (NumberFormatException e) {
                throw new InvalidRuleValueException("Invalid number for rule");
            }
        });
    }

}

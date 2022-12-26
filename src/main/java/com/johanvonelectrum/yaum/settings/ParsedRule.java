package com.johanvonelectrum.yaum.settings;

import net.minecraft.server.command.ServerCommandSource;

public interface ParsedRule<T> {
    String name();

    String description();

    String[] categories();

    String[] options();

    boolean strict();

    Rule.RuleSide ruleSide();

    T value();

    T defaultValue();

    T originalValue();

    void reset(ServerCommandSource source, boolean factory);

    void castAndSet(ServerCommandSource source, String value, boolean setDefault) throws InvalidRuleValueException;

    void set(ServerCommandSource source, T value, boolean setDefault) throws InvalidRuleValueException;

    boolean validate(ServerCommandSource source, T value);
}

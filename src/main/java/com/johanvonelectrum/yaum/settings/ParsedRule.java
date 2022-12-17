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

    void set(ServerCommandSource source, T value, boolean setDefault);

    boolean validate(ServerCommandSource source, T value);
}

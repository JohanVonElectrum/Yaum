package com.johanvonelectrum.yaum.settings;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public abstract class Validator<T> {

    public abstract String name();

    public abstract boolean validate(ServerCommandSource source, ParsedRule<T> changingRule, T newValue, String userInput);

    public String description() {
        return null;
    }

    public void notifyFailure(ServerCommandSource source, ParsedRule<T> currentRule, String providedValue)
    {
        source.sendError(new TranslatableText("error.yaum.invalid-rule-value", currentRule.name(), providedValue));
        if (description() != null)
            source.sendError(new TranslatableText("validator.yaum." + name() + ".description", currentRule.name(), providedValue));
    }

}

package com.johanvonelectrum.yaum.settings;

import com.johanvonelectrum.yaum.text.YaumText;
import net.minecraft.server.command.ServerCommandSource;

public abstract class Validator<T> {

    public abstract String name();

    public abstract boolean validate(ServerCommandSource source, ParsedRule<T> changingRule, T newValue, String userInput);

    public String description() {
        return "validator.yaum." + name() + ".description";
    }

    public void notifyFailure(ServerCommandSource source, ParsedRule<T> currentRule, String providedValue)
    {
        source.sendError(YaumText.translatable("error.yaum.invalid-rule-value", currentRule.name(), providedValue).asText());
        if (description() != null)
            source.sendError(
                    YaumText.translatable("error.yaum.invalid-rule-value.validator", providedValue, name(),
                    YaumText.translatable("validator.yaum." + name() + ".description", providedValue).asString()).asText()
            );
    }

}

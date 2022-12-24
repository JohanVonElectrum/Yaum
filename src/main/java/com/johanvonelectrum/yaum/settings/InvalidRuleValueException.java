package com.johanvonelectrum.yaum.settings;

import com.johanvonelectrum.yaum.text.YaumText;
import net.minecraft.server.command.ServerCommandSource;

/**
 * <p>An {@link Exception} thrown when the value given for a {@link ParsedRule} is invalid.</p>
 *
 * <p>It can hold a message to be sent to the executing source.</p>
 */
public class InvalidRuleValueException extends Exception {

    /**
     * <p>Constructs a new {@link InvalidRuleValueException} with a message that will be passed to the executing source</p>
     * @param cause The cause of the exception
     */
    public InvalidRuleValueException(String cause) {
        super(cause);
    }

    /**
     * <p>Constructs a new {@link InvalidRuleValueException} with no detail message, that therefore should not notify the source</p>
     */
    public InvalidRuleValueException() {
        super();
    }

    /**
     * <p>Notifies the given source with the exception's message if it exists, does nothing if it doesn't exist or it is {@code null}</p>
     * @param source The source to notify
     */
    public void notifySource(String ruleName, ServerCommandSource source) {
        if (getMessage() != null)
            source.sendError(YaumText.translatable("error.yaum.invalid-rule-value", ruleName, getMessage()).asText());
    }
}

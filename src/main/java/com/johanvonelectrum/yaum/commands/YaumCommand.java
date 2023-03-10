package com.johanvonelectrum.yaum.commands;

import com.johanvonelectrum.yaum.commands.arguments.RuleSuggestionProvider;
import com.johanvonelectrum.yaum.commands.arguments.ValueSuggestionProvider;
import com.johanvonelectrum.yaum.settings.InvalidRuleValueException;
import com.johanvonelectrum.yaum.settings.ParsedRule;
import com.johanvonelectrum.yaum.settings.SettingsManager;
import com.johanvonelectrum.yaum.text.YaumText;
import com.johanvonelectrum.yaum.text.format.YaumFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class YaumCommand {
    private static final RuleSuggestionProvider RULE_SUGGESTION_PROVIDER = new RuleSuggestionProvider();
    private static final ValueSuggestionProvider VALUE_SUGGESTION_PROVIDER = new ValueSuggestionProvider();
    private static final DynamicCommandExceptionType UNKNOWN_RULE_EXCEPTION = new DynamicCommandExceptionType(
            name -> YaumText.translatable("arguments.yaum.rule.notFound", name).asText()
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("yaum").then(
                literal("rule").then(
                        argument("ruleName", StringArgumentType.word())
                                .suggests(RULE_SUGGESTION_PROVIDER)
                                .executes(YaumCommand::peek).then(
                                        argument("value", StringArgumentType.greedyString())
                                                .suggests(VALUE_SUGGESTION_PROVIDER)
                                                .executes(YaumCommand::set)
                                )
                )
        );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int peek(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ParsedRule<?> rule = getRule(context, "ruleName");
        context.getSource().sendFeedback(YaumText.literal("**" + rule.name() + "**").asText(), false);
        context.getSource().sendFeedback(YaumText.translatable(rule.description()).asText(), false);
        context.getSource().sendFeedback(YaumText.translatable("command.yaum.yaum.peek.default", YaumFormatter.escape(rule.defaultValue().toString())).asText(), false);
        context.getSource().sendFeedback(YaumText.translatable("command.yaum.yaum.peek.value", YaumFormatter.escape(rule.value().toString())).asText(), false);
        if (rule.options().length > 0) context.getSource().sendFeedback(YaumText.translatable("command.yaum.yaum.peek.options",
                Arrays.stream(rule.options()).map(opt -> String.format(
                        "[\\[%s\\]](/yaum rule %s %s)", opt, rule.name(), opt
                )).collect(Collectors.joining(", "))
        ).asText(), false);
        return 1;
    }

    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ParsedRule<?> rule = getRule(context, "ruleName");
        String value = StringArgumentType.getString(context, "value");
        ServerCommandSource source = context.getSource();
        try {
            rule.castAndSet(source, value, false);
            source.sendFeedback(YaumText.translatable("command.yaum.yaum.set", YaumFormatter.escape(rule.name()), YaumFormatter.escape(rule.value().toString())).asText(), true); //TODO: send per op user with translations
        } catch (InvalidRuleValueException e) {
            e.notifySource(rule.name(), source);
        }
        return 1;
    }

    public static ParsedRule<?> getRule(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String ruleName = context.getArgument(name, String.class);
        ParsedRule<?> rule = SettingsManager.get(ruleName);
        if (rule == null) {
            throw UNKNOWN_RULE_EXCEPTION.create(ruleName);
        } else {
            return rule;
        }
    }
}

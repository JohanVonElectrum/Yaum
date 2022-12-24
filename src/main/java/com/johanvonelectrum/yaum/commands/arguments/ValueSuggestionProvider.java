package com.johanvonelectrum.yaum.commands.arguments;

import com.johanvonelectrum.yaum.settings.ParsedRule;
import com.johanvonelectrum.yaum.settings.SettingsManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ValueSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        if (context.getSource() != null) {
            String ruleName = context.getArgument("ruleName", String.class);
            ParsedRule<?> rule = SettingsManager.rules().get(ruleName);
            if (rule == null) return Suggestions.empty();
            return CommandSource.suggestMatching(rule.options(), builder);
        }
        return Suggestions.empty();
    }

}

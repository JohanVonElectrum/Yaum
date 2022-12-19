package com.johanvonelectrum.yaum.commands.arguments;

import com.johanvonelectrum.yaum.settings.SettingsManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class RuleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        if (context.getSource() != null) {
            return CommandSource.suggestMatching(SettingsManager.rules().keySet(), builder);
        }
        return Suggestions.empty();
    }

}

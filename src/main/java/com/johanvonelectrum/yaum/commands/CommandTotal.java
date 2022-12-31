package com.johanvonelectrum.yaum.commands;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.text.YaumText;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandTotal {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("total")
                .requires((player) -> YaumSettings.commandTotal)
                .then(argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                        .executes(context -> execute(context.getSource(), ScoreboardObjectiveArgumentType.getObjective(context, "objective")))
                        .then(argument("bots", BoolArgumentType.bool())
                                .executes(context -> execute(context.getSource(), ScoreboardObjectiveArgumentType.getObjective(context, "objective"), BoolArgumentType.getBool(context, "bots")))
                        )
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, ScoreboardObjective objective) {
        return execute(source, objective, YaumSettings.filterBotsInScores);
    }

    private static int execute(ServerCommandSource source, ScoreboardObjective objective, boolean bots) {
        Text total = YaumText.literal("[%s] Total: %d", objective.getDisplayName().asString(), getTotal(source, objective, bots)).asText();
        source.sendFeedback(total, false);

        return 1;
    }

    public static int getTotal(ServerCommandSource source, ScoreboardObjective objective, boolean bots) {
        int i = 0;
        for (ScoreboardPlayerScore score: source.getServer().getScoreboard().getAllPlayerScores(objective)) {
            if (!bots && source.getServer().getScoreboard().getPlayerTeam(score.getPlayerName()) == null)
                continue;
            i += score.getScore();
        }
        return i;
    }
}

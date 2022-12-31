package com.johanvonelectrum.yaum.commands;

import com.google.common.collect.Sets;
import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.text.YaumText;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandComputation {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("computation")
                .requires((player) -> YaumSettings.commandComputation)
                .then(literal("convert")
                        .then(argument("number", IntegerArgumentType.integer())
                                .then(argument("from_base", IntegerArgumentType.integer(1))
                                        .suggests(((context, builder) -> CommandSource.suggestMatching(getDefaultBases(), builder)))
                                        .then(argument("to_base", IntegerArgumentType.integer(1))
                                                .suggests(((context, builder) -> CommandSource.suggestMatching(getDefaultBases(), builder)))
                                                .executes(context -> convert(context.getSource(), IntegerArgumentType.getInteger(context, "number"), IntegerArgumentType.getInteger(context, "from_base"), IntegerArgumentType.getInteger(context, "to_base")))
                                        )
                                )
                        )
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static Collection<String> getDefaultBases() {
        return Sets.newLinkedHashSet(Arrays.asList("2", "8", "10", "16"));
    }

    private static int convert(ServerCommandSource source, int input, int base1, int base2) {
        source.sendFeedback(YaumText.translatable("rule.yaum.commandComputation.base", base1, input).asText(), false);
        source.sendFeedback(YaumText.translatable("rule.yaum.commandComputation.base", base2, Integer.toString(Integer.parseInt(input + "", base1), base2)).asText(), false);

        return 1;
    }

}

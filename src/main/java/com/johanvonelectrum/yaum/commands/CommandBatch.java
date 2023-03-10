package com.johanvonelectrum.yaum.commands;

import com.google.common.collect.Lists;
import com.johanvonelectrum.yaum.YaumSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Command "/batch" repeats a command N times
 * Syntax: /batch &lt;times&gt; [command]
 */
public class CommandBatch {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(literal("batch").requires(source -> YaumSettings.commandBatch));

        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("batch")
                .then(argument("size", IntegerArgumentType.integer(1))
                        .fork(literalCommandNode, context -> {
                            List<ServerCommandSource> list = Lists.newArrayList();

                            for (int i = 0; i < IntegerArgumentType.getInteger(context, "size"); i++) {
                                list.add(context.getSource());
                            }

                            return list;
                        })
                ).then(literal("run").redirect(dispatcher.getRoot()));

        dispatcher.register(literalArgumentBuilder);
    }
}
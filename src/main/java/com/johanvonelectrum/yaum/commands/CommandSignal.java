package com.johanvonelectrum.yaum.commands;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.text.YaumText;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandSignal {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("signal")
                .requires((player) -> player.hasPermissionLevel(2) && YaumSettings.commandSignal)
                .then(argument("value", IntegerArgumentType.integer(1, 897))
                        .executes(context -> execute(context.getSource(), IntegerArgumentType.getInteger(context, "value")))
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, int value) throws CommandSyntaxException {
        if (!YaumSettings.commandSignal || source == null)
            return 0;

        ItemStack item = Items.BARREL.getDefaultStack();
        NbtCompound tags = new NbtCompound();
        NbtList itemsTag = new NbtList();

        for (int slot = 0, count = (int) Math.ceil(27 * (value - 1) / 14D); count > 0; slot++, count -= 64) {
            NbtCompound slotTag = new NbtCompound();
            slotTag.putByte("Slot", (byte) slot);
            slotTag.putString("id", Registry.ITEM.getId(Items.WHITE_SHULKER_BOX).toString());
            slotTag.putByte("Count", (byte) Math.min(64, count));
            itemsTag.add(slotTag);
        }

        NbtCompound tag = new NbtCompound();
        tag.put("Items", itemsTag);
        tags.put("BlockEntityTag", tag);

        BaseText text = (BaseText) YaumText.literal("Signal: %d", value).asText();
        text.setStyle(text.getStyle().withColor(Formatting.RED));
        item.setNbt(tags);
        item.setCustomName(text);
        source.getPlayer().giveItemStack(item);

        System.out.println(item.getNbt());

        return 1;
    }
}

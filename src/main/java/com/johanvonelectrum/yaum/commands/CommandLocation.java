package com.johanvonelectrum.yaum.commands;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.helpers.PlayerHelper;
import com.johanvonelectrum.yaum.text.YaumText;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandLocation {

    private static final String[] DIM_RAW = new String[]{"overworld", "the_nether", "the_end"};
    public static final BaseText[] DIMENSIONS = new BaseText[]{
            (BaseText) new LiteralText("Overworld").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)),
            (BaseText) new LiteralText("Nether").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)),
            (BaseText) new LiteralText("End").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE))
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("location")
                .requires((player) -> YaumSettings.commandLocation)
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> locate(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int locate(ServerCommandSource source, ServerPlayerEntity player) {
        if (!YaumSettings.commandLocation || player == null)
            return 0;

        BlockPos coords = PlayerHelper.getCoords(player);
        BlockPos netherCoords = new BlockPos(coords.getX() / 8, coords.getY() / 8, coords.getZ() / 8);

        BaseText playerText = (BaseText) player.getDisplayName();
        playerText
                .setStyle(playerText.getStyle().withClickEvent(
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getEntityName())))
                .setStyle(playerText.getStyle().withColor(Formatting.YELLOW));

        String dimStr = PlayerHelper.getDimensionName(player);
        BaseText dimensionText = DIMENSIONS[ArrayUtils.indexOf(DIM_RAW, dimStr)];
        BaseText coordsText = (BaseText) YaumText.literal("[x: %d, y: %d, z: %d]", coords.getX(), coords.getY(), coords.getZ()).asText();
        coordsText.setStyle(coordsText.getStyle().withColor(Formatting.AQUA));
        BaseText netherCoordsText = (BaseText) YaumText.literal("(%d %d %d)", netherCoords.getX(), netherCoords.getY(), netherCoords.getZ()).asText();

        BaseText finalText = new LiteralText("");
        finalText.append(playerText).append(" @ ").append(dimensionText).append(coordsText);
        if (dimStr.equals("overworld"))
            finalText.append(" -> ").append(DIMENSIONS[1]).append(netherCoordsText);

        source.sendFeedback(finalText, false);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15 * 20, 0, false, false));

        return 1;
    }
}
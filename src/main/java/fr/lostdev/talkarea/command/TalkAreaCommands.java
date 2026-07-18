package fr.lostdev.talkarea.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;


public class TalkAreaCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("talkarea")
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal("/talkarea info"), false);
                    context.getSource().sendSuccess(() -> Component.literal("/talkarea toggle <true/false>"), false);
                    context.getSource().sendSuccess(() -> Component.literal("/talkarea distance <distance>"), false);
                    context.getSource().sendSuccess(() -> Component.literal("/talkarea listen <true/false>"), false);
                    return 1;
                })

                .then(Commands.literal("info")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.translatable("talkarea.command.info"), false);

                            Player player = context.getSource().getPlayerOrException();
                            boolean toggle = player.getData(TalkAreaData.TALKAREA_TOGGLE);
                            int distance = player.getData(TalkAreaData.TALKAREA_DISTANCE);
                            boolean listenToggle = player.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE);

                            if (toggle) {
                                context.getSource().sendSuccess(() -> Component.translatable("talkarea.command.enable_distance", distance).withStyle(ChatFormatting.GREEN), false);
                                context.getSource().sendSuccess(listenToggle ? () -> Component.translatable("talkarea.command.listen_enable").withStyle(ChatFormatting.GREEN) : () -> Component.translatable("talkarea.command.listen_disable").withStyle(ChatFormatting.RED), false);
                            }
                            else {
                                context.getSource().sendSuccess(() -> Component.translatable("talkarea.command.disable").withStyle(ChatFormatting.RED), false);
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("toggle")
                        .executes(context -> {
                                CommandSourceStack source = context.getSource();
                                boolean toggle = source.getPlayerOrException().getData(TalkAreaData.TALKAREA_TOGGLE);
                                toggleTalkArea(source, !toggle);
                                return 1;
                        })

                        .then(Commands.argument("toggle", BoolArgumentType.bool())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    boolean toggle = context.getArgument("toggle", Boolean.class);
                                    toggleTalkArea(source, toggle);
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("distance")
                        .then(Commands.argument("distance", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    int distance = context.getArgument("distance", Integer.class);
                                    setTalkAreaDistance(source, distance);
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("listen")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            boolean toggle = source.getPlayerOrException().getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE);
                            toggleTalkAreaListen(source, !toggle);
                            return 1;
                        })

                        .then(Commands.argument("toggle", BoolArgumentType.bool())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    boolean toggle = context.getArgument("toggle", Boolean.class);
                                    toggleTalkAreaListen(source, toggle);
                                    return 1;
                                })
                        )
                )

        );
    }

    /**
     * Toggle the talkarea
     * @param source the player whose value we are changing
     * @param toggle if we enable or disables the talkArea
     */
    private static void toggleTalkArea(CommandSourceStack source, boolean toggle) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        player.setData(TalkAreaData.TALKAREA_TOGGLE, toggle);
        if (toggle) {
            source.sendSuccess(() -> Component.translatable("talkarea.command.enable_distance", player.getData(TalkAreaData.TALKAREA_DISTANCE)).withStyle(ChatFormatting.GREEN), false);
        } else {
            source.sendSuccess(() -> Component.translatable("talkarea.command.disable").withStyle(ChatFormatting.RED), false);
            if (player.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {
                player.setData(TalkAreaData.TALKAREA_LISTEN_TOGGLE, false);
                source.sendSuccess(() -> Component.translatable("talkarea.command.listen_disable").withStyle(ChatFormatting.RED), false);
            }
        }

        TalkAreaData.synchronizeDataWithClient(player);
    }

    /**
     * Set the talkarea distance.
     * @param source the player whose value we are changing
     * @param distance distance of the talkarea
     */
    private static void setTalkAreaDistance(CommandSourceStack source, int distance) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        player.setData(TalkAreaData.TALKAREA_DISTANCE, distance);
        player.setData(TalkAreaData.TALKAREA_TOGGLE, true);
        source.sendSuccess(() -> Component.translatable("talkarea.command.enable_distance", distance).withStyle(ChatFormatting.GREEN), false);
        if (player.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {
            source.sendSuccess(() -> Component.translatable("talkarea.command.listen_enable").withStyle(ChatFormatting.GREEN), false);
        }

        TalkAreaData.synchronizeDataWithClient(player);
    }

    /**
     * Toggle the talkarea listen
     * @param source the player whose value we are changing
     * @param toggle if we enable or disables the talkArea listen
     */
    private static void toggleTalkAreaListen(CommandSourceStack source, boolean toggle) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        player.setData(TalkAreaData.TALKAREA_LISTEN_TOGGLE, toggle);
        if (toggle){
            player.setData(TalkAreaData.TALKAREA_TOGGLE, true);
            source.sendSuccess(() -> Component.translatable("talkarea.command.enable_distance", player.getData(TalkAreaData.TALKAREA_DISTANCE)).withStyle(ChatFormatting.GREEN), false);
            source.sendSuccess(() -> Component.translatable("talkarea.command.listen_enable").withStyle(ChatFormatting.GREEN), false);
        }
        else {
            source.sendSuccess(() -> Component.translatable("talkarea.command.listen_disable").withStyle(ChatFormatting.RED), false);
        }

        TalkAreaData.synchronizeDataWithClient(player);
    }
}

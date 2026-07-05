package fr.lostdev.talkarea.command;

import com.mojang.brigadier.CommandDispatcher;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class TalkAreaCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("talkarea")
                .executes(command -> {
                    command.getSource().sendSuccess(() -> Component.literal("/talkarea info"), false);
                    command.getSource().sendSuccess(() -> Component.literal("/talkarea toggle <true/false>"), false);
                    command.getSource().sendSuccess(() -> Component.literal("/talkarea distance <distance>"), false);
                    command.getSource().sendSuccess(() -> Component.literal("/talkarea listen <true/false>"), false);
                    return 1;
                })

                .then(Commands.literal("info")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.translatable("lustcraft.command.talkarea.info"), false);
                            return 1;
                        })

                        .then(Commands.literal("toggle")
                                .executes(context -> {
                                    if (context.getSource().isPlayer()) {
                                        boolean toggle = context.getSource().getPlayerOrException().getData(TalkAreaData.TALKAREA_TOGGLE);
                                        toggleTalkArea(!toggle);
                                        return 1;
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("lustcraft.command.talkarea.toggle.not_player"));
                                        return 0;
                                    }
                                })
                        )
                )
        );
    }

    /**
     * Toggle the talkArea on or off.
     * @param toggle if we enable or disables the talkArea
     */
    private static void toggleTalkArea(boolean toggle) {

    }
}

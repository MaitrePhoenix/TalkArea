package fr.lostdev.talkarea.command;

import com.mojang.brigadier.CommandDispatcher;
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
                        .executes(command -> {
                            command.getSource().sendSuccess(() -> Component.translatable("lustcraft.command.talkarea.info"), false);
                            return 1;
                        })
                )
        );
    }
}

package fr.lostdev.talkarea.event;

import fr.lostdev.talkarea.ChatTypeList;
import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = TalkArea.MODID)
public class ServerEvent {

    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMessageSend(ServerChatEvent event) {
        ServerPlayer sender = event.getPlayer();
        String message = event.getRawText();

        //s'il s'agit d'une commande, on ne l'annule pas
        if (message.startsWith("/")) {
            return;
        }

        if (sender.getData(TalkAreaData.TALKAREA_TOGGLE)) {
            event.setCanceled(true);
            for (ServerPlayer receiver : sender.serverLevel().getServer().getPlayerList().getPlayers()) {
                int distance = sender.getData(TalkAreaData.TALKAREA_DISTANCE);
                double distanceSquared = distance * distance;
                double actualDistanceSquared = sender.distanceToSqr(receiver);
                if (actualDistanceSquared <= distanceSquared) {

                    OutgoingChatMessage chatMessage = new OutgoingChatMessage.Disguised(event.getMessage());

                    receiver.sendChatMessage(chatMessage, receiver.shouldFilterMessageTo(sender), ChatType.bind(ChatTypeList.TALKAREA_CHAT_TYPE, sender));
                }
            }
        }
    }*/

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TalkAreaData.synchronizeDataWithClient(serverPlayer);

            if (serverPlayer.getData(TalkAreaData.TALKAREA_TOGGLE)) {
                serverPlayer.sendSystemMessage(Component.translatable("talkarea.command.enable_distance", serverPlayer.getData(TalkAreaData.TALKAREA_DISTANCE)).withStyle(ChatFormatting.GREEN));
                if (serverPlayer.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {
                    serverPlayer.sendSystemMessage(Component.translatable("talkarea.command.listen_enable").withStyle(ChatFormatting.GREEN));
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("talkarea.command.listen_disable").withStyle(ChatFormatting.RED));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("talkarea.command.disable").withStyle(ChatFormatting.RED));
            }
        }
    }
}

package fr.lostdev.talkarea.mixin;

import fr.lostdev.talkarea.ChatTypeList;
import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    /**
     * Intercept the message sent by the player if they have TalkArea enabled, and instead send it separately to each person within range
     */
    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"), cancellable = true)
    private void talkarea$restrictBroadcast(PlayerChatMessage message, Predicate<ServerPlayer> shouldFilterMessageTo, ServerPlayer sender, ChatType.Bound boundChatType, CallbackInfo ci) {
        if (sender == null)
            return;

        //if the sender as talkarea toggle on
        //we only use the talkarea for chat and emote messages
        if (sender.getData(TalkAreaData.TALKAREA_TOGGLE) && (boundChatType.chatType().is(ChatType.CHAT) || boundChatType.chatType().is(ChatType.EMOTE_COMMAND))) {

            ci.cancel();
            TalkArea.LOGGER.info("{}", boundChatType.decorate(message.decoratedContent()).getString());

            for (ServerPlayer receiver : sender.serverLevel().getServer().getPlayerList().getPlayers()) {
                int distance = sender.getData(TalkAreaData.TALKAREA_DISTANCE);
                double distanceSquared = distance * distance;
                double actualDistanceSquared = sender.distanceToSqr(receiver);
                if (actualDistanceSquared <= distanceSquared) {

                    OutgoingChatMessage chatMessage = new OutgoingChatMessage.Player(message);

                    if (boundChatType.chatType().is(ChatType.EMOTE_COMMAND)) {
                        receiver.sendChatMessage(chatMessage, receiver.shouldFilterMessageTo(sender), ChatType.bind(ChatTypeList.TALKAREA_EMOTE_CHAT_TYPE, sender));
                    } else {
                        receiver.sendChatMessage(chatMessage, receiver.shouldFilterMessageTo(sender), ChatType.bind(ChatTypeList.TALKAREA_CHAT_TYPE, sender));
                    }
                }
            }
        }
    }
}

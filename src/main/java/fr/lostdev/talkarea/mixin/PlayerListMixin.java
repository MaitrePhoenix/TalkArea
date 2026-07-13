package fr.lostdev.talkarea.mixin;

import fr.lostdev.talkarea.ChatTypeList;
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

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"), cancellable = true)
    private void talkarea$restrictBroadcast(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound boundChatType, CallbackInfo ci) {
        if (sender.getData(TalkAreaData.TALKAREA_TOGGLE)) {
            ci.cancel();
            for (ServerPlayer receiver : sender.serverLevel().getServer().getPlayerList().getPlayers()) {
                int distance = sender.getData(TalkAreaData.TALKAREA_DISTANCE);
                double distanceSquared = distance * distance;
                double actualDistanceSquared = sender.distanceToSqr(receiver);
                if (actualDistanceSquared <= distanceSquared) {

                    OutgoingChatMessage chatMessage = new OutgoingChatMessage.Player(message);

                    receiver.sendChatMessage(chatMessage, receiver.shouldFilterMessageTo(sender), ChatType.bind(ChatTypeList.TALKAREA_CHAT_TYPE, sender));
                }
            }
        }
    }
}

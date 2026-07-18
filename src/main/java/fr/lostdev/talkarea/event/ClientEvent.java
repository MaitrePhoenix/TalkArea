package fr.lostdev.talkarea.event;

import fr.lostdev.talkarea.ChatTypeList;
import fr.lostdev.talkarea.FontList;
import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;

import java.util.UUID;

@EventBusSubscriber(modid = TalkArea.MODID, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void onMessageReceived(ClientChatReceivedEvent event){
        UUID senderUUID = event.getSender();
        assert Minecraft.getInstance().level != null;

        Player sender = Minecraft.getInstance().level.getPlayerByUUID(senderUUID);
        Player receiver = Minecraft.getInstance().player;

        if (sender == null || receiver == null){
            return;
        }

        if (receiver.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {

            int distance = receiver.getData(TalkAreaData.TALKAREA_DISTANCE);
            double distanceSquared = distance * distance;
            double actualDistanceSquared = sender.distanceToSqr(receiver);
            if (actualDistanceSquared > distanceSquared) {
                event.setCanceled(true);
            }
        }

        if (receiver.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE) || (event.getBoundChatType() != null && event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_CHAT_TYPE))) {
            Component tooltipText = Component.translatable("talkarea.chat.tooltip.talkarea", sender.getDisplayName(), sender.distanceTo(receiver)).withStyle(ChatFormatting.GRAY);
            Style iconStyle = Style.EMPTY.withFont(FontList.FONT_ICONS).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText));

            event.setMessage(event.getMessage().copy().withStyle(iconStyle));
        }
    }
}

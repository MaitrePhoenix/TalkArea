package fr.lostdev.talkarea.event;

import fr.lostdev.talkarea.ChatTypeList;
import fr.lostdev.talkarea.FontList;
import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
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

        Player receiver = Minecraft.getInstance().player;
        if (receiver != null && receiver.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {
            assert Minecraft.getInstance().level != null;
            Player sender = Minecraft.getInstance().level.getPlayerByUUID(senderUUID);

            if (sender == null){
                return;
            }

            int distance = receiver.getData(TalkAreaData.TALKAREA_DISTANCE);
            double distanceSquared = distance * distance;
            double actualDistanceSquared = sender.distanceToSqr(receiver);
            if (actualDistanceSquared > distanceSquared) {
                event.setCanceled(true);
            }
        }

        if (event.getBoundChatType() != null && event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_CHAT_TYPE)) {
            Component tooltipText = Component.literal("test");
            Style iconStyle = Style.EMPTY.withFont(FontList.FONT_ICONS).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText));

            event.setMessage(event.getMessage().copy().withStyle(iconStyle));
        }
    }
}

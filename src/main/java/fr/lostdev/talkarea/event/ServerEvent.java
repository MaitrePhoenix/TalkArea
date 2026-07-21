package fr.lostdev.talkarea.event;

import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.config.ServerConfig;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = TalkArea.MODID)
public class ServerEvent {

    /**
     * when a player logs in, we synchronize the talkarea data with the client, and send a message to the player to inform them of their current talkarea settings.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (ServerConfig.INSTANCE.FORCE_TALKAREA_ON_SERVER.getAsBoolean()) {
                serverPlayer.setData(TalkAreaData.TALKAREA_TOGGLE.get(), true);
                serverPlayer.setData(TalkAreaData.TALKAREA_DISTANCE.get(), ServerConfig.INSTANCE.FORCE_TALKAREA_DISTANCE.getAsInt());
            }

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

    @SubscribeEvent
    public static void onConfigReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == ServerConfig.CONFIG_SPEC && ServerLifecycleHooks.getCurrentServer() != null) {
            PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();

            boolean toggle = ServerConfig.INSTANCE.FORCE_TALKAREA_ON_SERVER.getAsBoolean();
            int distance = ServerConfig.INSTANCE.FORCE_TALKAREA_DISTANCE.getAsInt();

            for (ServerPlayer player : playerList.getPlayers()) {
                if (toggle) {
                    player.setData(TalkAreaData.TALKAREA_TOGGLE.get(), true);
                    player.setData(TalkAreaData.TALKAREA_DISTANCE.get(), distance);

                    player.sendSystemMessage(Component.translatable("talkarea.command.enable_distance", distance).withStyle(ChatFormatting.GREEN));
                } else {
                    player.setData(TalkAreaData.TALKAREA_TOGGLE.get(), false);

                    player.sendSystemMessage(Component.translatable("talkarea.command.disable").withStyle(ChatFormatting.RED));
                }
                TalkAreaData.synchronizeDataWithClient(player);
            }
        }
    }
}

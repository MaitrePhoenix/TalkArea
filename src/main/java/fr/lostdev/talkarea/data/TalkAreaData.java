package fr.lostdev.talkarea.data;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Is used to stock data on the player about talkarea
 */
public class TalkAreaData {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "talkarea");

    public static final Supplier<AttachmentType<Boolean>> TALKAREA_TOGGLE = ATTACHMENTS.register(
            "talkarea_toggle", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Integer>> TALKAREA_DISTANCE = ATTACHMENTS.register(
            "talkarea_distance", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Boolean>> TALKAREA_LISTEN_TOGGLE = ATTACHMENTS.register(
            "talkarea_listen_toggle", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build()
    );

    public static void init(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}

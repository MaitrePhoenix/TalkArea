package fr.lostdev.talkarea.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {
    public static final ServerConfig INSTANCE;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue FORCE_TALKAREA_ON_SERVER;
    public final ModConfigSpec.IntValue FORCE_TALKAREA_DISTANCE;

    static {
        Pair<ServerConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private ServerConfig(ModConfigSpec.Builder builder) {
        builder.push("server");

        FORCE_TALKAREA_ON_SERVER = builder
                .comment("Forces talkarea to be enabled for all players on the server if enabled. Default is false.")
                .define("force_talkarea_on_server", false);

        FORCE_TALKAREA_DISTANCE = builder
                .comment("Sets the distance for talkarea if force_talkarea_on_server is enabled.")
                .defineInRange("force_talkarea_distance", 100, 1, Integer.MAX_VALUE);

        builder.pop();
    }
}

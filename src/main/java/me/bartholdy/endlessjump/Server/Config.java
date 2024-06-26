package me.bartholdy.endlessjump.Server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

public record Config(
        @JsonProperty("network")
        @NotNull Network networkData,

        @JsonProperty("proxy")
        @NotNull Proxy proxyData,

        @JsonProperty("server")
        @NotNull Server serverData,

        @JsonProperty("instance")
        @NotNull Instance instanceData,

        @JsonProperty("commands")
        @NotNull Commands commandsData
) {

    public Config() {
        this(
                new Config.Network(),
                new Config.Proxy(),
                new Config.Server(),
                new Config.Instance(),
                new Config.Commands()
        );
    }

    @JsonRootName("network")
    public record Network(
            @JsonProperty("ip")
            @NotNull String ip,

            @JsonProperty("port")
            @Range(from = 1025, to = 65536) int port,

            @JsonProperty("open_to_Lan")
            boolean openToLan
    ) {
        public Network() {
            this(
                    "127.0.0.1",
                    25565,
                    false
            );
        }
    }

    @JsonRootName("proxy")
    public record Proxy(
            @JsonProperty("enabled")
            boolean enabled,

            @JsonProperty("type")
            @NotNull String type,

            @JsonProperty("secret")
            @NotNull String secret
    ) {
        public Proxy() {
            this(
                    false,
                    "",
                    ""
            );
        }
    }

    @JsonRootName("server")
    public record Server(
            @JsonProperty("ticks_per_second")
            @Range(from = 1, to = 128) int ticksPerSecond,

            @JsonProperty("chunk_view_distance")
            @Range(from = 2, to = 32) int chunkViewDistance,

            @JsonProperty("entity_view_distance")
            @Range(from = 2, to = 32) int entityViewDistance,

            @JsonProperty("online_mode")
            boolean onlineMode,

            @JsonProperty("optifine_support")
            boolean optifineSupport,

            @JsonProperty("terminal")
            boolean terminal,

            @JsonProperty("benchmark")
            boolean benchmark
    ) {
        public Server() {
            this(
                    20,
                    8,
                    6,
                    true,
                    true,
                    false,
                    false
            );
        }
    }

    @JsonRootName("instance")
    public record Instance(
            @JsonProperty("world")
            @NotNull String worldName,

            @JsonProperty("enabled")
            boolean enabled,

            @JsonProperty("type")
            @NotNull String typeName
    ) {
        public Instance() {
            this(
                    "",
                    false,
                    ""
            );
        }
    }

    @JsonRootName("commands")
    public record Commands(

            @JsonProperty("help")
            boolean help,

            @JsonProperty("commands")
            boolean command,

            @JsonProperty("plugins")
            boolean plugins,

            @JsonProperty("give")
            boolean give,

            @JsonProperty("gamemode")
            boolean gamemode,

            @JsonProperty("test")
            boolean test

    ) {
        public Commands() {
            this(
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            );
        }
    }

}
package me.bartholdy.endlessjump.Server.io;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.bartholdy.endlessjump.Server.Config;
import org.jetbrains.annotations.NotNull;

public class ConfigDeserializer extends StdDeserializer<Config> {

    public ConfigDeserializer() {
        super(Config.class);
    }

    @Override
    public Config deserialize(@NotNull JsonParser parser, @NotNull DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode networkDataNode    =   node.get("network");
        String ip                   =   networkDataNode.get("ip").asText("127.0.0.1");
        int port                    =   networkDataNode.get("port").asInt(25565);
        boolean	openToLan           =   networkDataNode.get("open_to_lan").asBoolean(false);
	
        JsonNode proxyDataNode      =   node.get("proxy");
        boolean proxy_enabled       =   proxyDataNode.get("enabled").asBoolean(false);
        String proxy_type           =   proxyDataNode.get("type").asText();
        String proxy_secret         =   proxyDataNode.get("secret").asText();

        JsonNode serverDataNode     =   node.get("server");
        int tick_per_second         =   serverDataNode.get("ticks_per_second").asInt(20);
        int chunk_view_distance     =   serverDataNode.get("chunk_view_distance").asInt(8);
        int entity_view_distance    =   serverDataNode.get("entity_view_distance").asInt(6);
        boolean online_mode         =   serverDataNode.get("online_mode").asBoolean(true);
        boolean optifine_support    =   serverDataNode.get("optifine_support").asBoolean(true);
        boolean terminal            =   serverDataNode.get("terminal").asBoolean(false);
        boolean benchmark           =   serverDataNode.get("benchmark").asBoolean(false);

        JsonNode instanceDataNode   =   node.get("instance");
        String default_instance_name=	instanceDataNode.get("world").asText();
        boolean instance_enabled    =   instanceDataNode.get("enabled").asBoolean(false);
        String instance_type        =   instanceDataNode.get("type").asText();

        JsonNode commandsDataNode   = node.get("commands");
        boolean commandCOMMANDS     = commandsDataNode.get("commands").asBoolean(true);
        boolean commandHELP         = commandsDataNode.get("help").asBoolean(true);
        boolean commandGAMEMODE     = commandsDataNode.get("gamemode").asBoolean(true);
        boolean commandGIVE         = commandsDataNode.get("give").asBoolean(true);
        boolean commandPLUGINS      = commandsDataNode.get("plugins").asBoolean(true);
        boolean commandTEST         = commandsDataNode.get("test").asBoolean(true);

        Config.Network networkData = new Config.Network(
                ip,
                port,
                openToLan
        );

        Config.Proxy proxyData     = new Config.Proxy(
                proxy_enabled,
                proxy_type,
                proxy_secret
        );

        Config.Server serverData   = new Config.Server(
                tick_per_second,
                chunk_view_distance,
                entity_view_distance,
                online_mode,
                optifine_support,
                terminal,
                benchmark
        );

        Config.Instance instanceData = new Config.Instance(
        		default_instance_name,
                instance_enabled,
                instance_type
        );

        Config.Commands commandsData = new Config.Commands(
                commandHELP,
                commandPLUGINS,
                commandCOMMANDS,
                commandGAMEMODE,
                commandGIVE,
                commandTEST
        );

        return new Config(networkData, proxyData, serverData, instanceData, commandsData);
    }

}
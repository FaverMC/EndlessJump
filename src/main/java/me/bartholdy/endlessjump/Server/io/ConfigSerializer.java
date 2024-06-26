package me.bartholdy.endlessjump.Server.io;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import me.bartholdy.endlessjump.Server.Config;

public class ConfigSerializer extends StdSerializer<Config> {

    public ConfigSerializer() {
        super(Config.class);
    }

    @Override
    public void serialize(
            @NotNull Config Config,
            @NotNull JsonGenerator generator,
            @NotNull SerializerProvider provider)
            throws IOException
    {
        Config.Network networkData = Config.networkData();
        Config.Proxy proxyData     = Config.proxyData();
        Config.Server serverData   = Config.serverData();
        Config.Commands commandsData = Config.commandsData();

        generator.writeStartObject();
        generator.writeObjectFieldStart("network");
        generator.writeStringField("ip", "127.0.0.1");
        generator.writeNumberField("port", 25565);
        generator.writeBooleanField("open_to_lan", false);
        generator.writeEndObject();

        generator.writeObjectFieldStart("proxy");
        generator.writeBooleanField("enabled", false);
        generator.writeStringField("type", "");
        generator.writeStringField("secret", "");
        generator.writeEndObject();

        generator.writeObjectFieldStart("server");
        generator.writeNumberField("ticks_per_second", 20);
        generator.writeNumberField("chunk_view_distance", 8);
        generator.writeNumberField("entity_view_distance", 6);
        generator.writeBooleanField("online_mode", false);
        generator.writeBooleanField("optifine_support", true);
        generator.writeBooleanField("terminal", false);
        generator.writeBooleanField("benchmark", false);
        generator.writeEndObject();

        generator.writeObjectFieldStart("instance");
        generator.writeStringField("world", "world");
        generator.writeBooleanField("enabled", false);
        generator.writeStringField("type", "void");
        generator.writeEndObject();

        generator.writeObjectFieldStart("commands");
        generator.writeBooleanField("help", true);
        generator.writeBooleanField("give", true);
        generator.writeBooleanField("test", true);
        generator.writeBooleanField("plugins", true);
        generator.writeBooleanField("commands", true);
        generator.writeBooleanField("gamemode", true);
        generator.writeEndObject();

        generator.writeEndObject();
    }

}
package io.github.zero88.msa.blueprint.dto.jackson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.github.zero88.msa.blueprint.dto.PlainType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class JsonModule {

    public static final Module BASIC;

    static {
        BASIC = new SimpleModule().addDeserializer(JsonObject.class, new JsonObjectDeserializer())
                                  .addDeserializer(JsonArray.class, new JsonArrayDeserializer())
                                  .addSerializer(PlainType.class, new PlainTypeSerializer());
    }


    private static class JsonObjectDeserializer extends JsonDeserializer<JsonObject> {

        @Override
        public JsonObject deserialize(JsonParser p, DeserializationContext context) throws IOException {
            return new JsonObject(context.readValue(p, Map.class));
        }

    }


    private static class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

        @Override
        public JsonArray deserialize(JsonParser p, DeserializationContext context) throws IOException {
            return new JsonArray(context.readValue(p, List.class));
        }

    }


    private static class PlainTypeSerializer extends JsonSerializer<PlainType> {

        @Override
        public void serialize(PlainType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.type());
        }

    }

}

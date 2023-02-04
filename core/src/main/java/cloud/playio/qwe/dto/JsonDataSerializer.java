package cloud.playio.qwe.dto;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.HasLogger;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
public final class JsonDataSerializer implements Function<Object, JsonObject>, HasLogger {

    @NonNull
    @Default
    private final String backupKey = JsonData.SUCCESS_KEY;
    @Default
    private final boolean lenient = false;
    @NonNull
    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    @Override
    public JsonObject apply(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof String) {
            try {
                return new JsonObject(mapper.readValue((String) obj, Map.class));
            } catch (IOException e) {
                logger().trace("Failed mapping to json. Fallback to construct Json from plain string", e);
                return decode(obj, e);
            }
        }
        if (obj instanceof JsonData) {
            return ((JsonData) obj).toJson(mapper);
        }
        if (obj instanceof JsonObject) {
            return (JsonObject) obj;
        }
        if (obj instanceof JsonArray) {
            return decode(obj, "Failed to decode from JsonArray");
        }
        if (obj instanceof Collection) {
            return decode(obj, "Failed to decode from Collection");
        }
        if (obj instanceof Buffer) {
            return apply(obj.toString());
        }
        try {
            return new JsonObject((Map<String, Object>) mapper.convertValue(obj, Map.class));
        } catch (IllegalArgumentException e) {
            logger().trace("Failed mapping to json. Fallback to construct Json from plain object", e);
            return decode(obj, e);
        }
    }

    private JsonObject decode(Object obj, Exception e) {
        return decode(obj, "Failed to decode " + e.getMessage());
    }

    private JsonObject decode(Object obj, String msg) {
        if (lenient) {
            return new JsonObject().put(backupKey, obj);
        }
        throw new DecodeException(msg);
    }

}

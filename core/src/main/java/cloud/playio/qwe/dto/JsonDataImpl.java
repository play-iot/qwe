package cloud.playio.qwe.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.QWEException;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
final class JsonDataImpl extends HashMap<String, Object> implements JsonData {

    private static final Logger logger = LogManager.getLogger(JsonDataImpl.class);

    JsonDataImpl(@NonNull Map<String, Object> map) { this.putAll(map); }

    JsonDataImpl(@NonNull JsonObject json)         { this(json.getMap()); }

    static JsonData tryParse(@NonNull Buffer buffer, boolean isJson, String backupKey, boolean isWrapper) {
        if (buffer.length() == 0) {
            return new JsonDataImpl();
        }
        try {
            if (isWrapper) {
                return new JsonDataImpl(new JsonObject().put(backupKey, buffer.toJson()));
            }
            return new JsonDataImpl(buffer.toJsonObject());
        } catch (DecodeException e) {
            logger.trace("Failed to parse json. Try json array", e);
            JsonObject data = new JsonObject();
            try {
                data.put(backupKey, buffer.toJsonArray());
            } catch (DecodeException ex) {
                if (isJson) {
                    throw new QWEException(ErrorCode.INVALID_ARGUMENT,
                                           "Cannot parse json data. Received data: " + buffer, ex);
                }
                logger.trace("Failed to parse json array. Use text", ex);
                //TODO check length, check encode
                data.put(backupKey, buffer.toString());
            }
            return new JsonDataImpl(data);
        }
    }

    static JsonData tryParse(@NonNull Object obj) {
        if (obj instanceof JsonData) {
            return (JsonData) obj;
        }
        if (obj instanceof Buffer) {
            return tryParse((Buffer) obj, true, SUCCESS_KEY, false);
        }
        return new JsonDataImpl(
            JsonDataSerializer.builder().backupKey(SUCCESS_KEY).lenient(true).mapper(MAPPER).build().apply(obj));
    }

}

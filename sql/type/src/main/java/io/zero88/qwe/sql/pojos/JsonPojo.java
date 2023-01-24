package io.zero88.qwe.sql.pojos;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper Pojo with exclude {@code null} value.
 *
 * @param <T> {@link JsonRecord}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonPojo<T extends JsonRecord> implements JsonData {

    public static final ObjectMapper MAPPER = JsonData.MAPPER.copy()
                                                             .setSerializationInclusion(Include.NON_NULL)
                                                             .addMixIn(Map.class, PropertyFilterMixIn.class)
                                                             .addMixIn(JsonObject.class, PropertyFilterMixIn.class)
                                                             .addMixIn(JsonRecord.class, PropertyFilterMixIn.class)
                                                             .addMixIn(JsonData.class, PropertyFilterMixIn.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPojo.class);
    @Getter
    @JsonIgnore
    private final T pojo;

    public ObjectMapper getMapper() {
        return MAPPER;
    }

    public static <T extends JsonRecord> JsonPojo<T> from(@NonNull T pojo) {
        return new JsonPojo<T>(pojo);
    }

    //    public static <T extends JsonRecord> JsonPojo<T> from(@NonNull T pojo, @NonNull ObjectMapper mapper) {
    //        return new JsonPojo<T>(pojo, mapper);
    //    }

    public static <T extends JsonRecord> JsonObject merge(@NonNull T from, @NonNull T to) {
        return from.toJson().mergeIn(new JsonPojo<T>(to).toJson(), true);
    }

    public static <T extends JsonRecord> JsonObject merge(@NonNull T from, @NonNull JsonObject to) {
        return from.toJson().mergeIn(to, true);
    }

    public static JsonObject merge(@NonNull JsonObject from, @NonNull JsonObject to) {
        return from.mergeIn(to, true);
    }

    @Override
    public JsonObject toJson() {
        return toJson(MAPPER);
    }

    @Override
    public JsonObject toJson(@NonNull ObjectMapper mapper) {
        JsonObject json = this.pojo.toJson();
        try {
            return mapper.readValue(mapper.writeValueAsBytes(json.getMap()), JsonObject.class);
        } catch (IOException e) {
            LOGGER.warn("Cannot re-parse pojo {}", pojo.getClass(), e);
            return json;
        }
    }

    @Override
    public JsonObject toJson(@NonNull Set<String> ignoreFields) {
        return toJson(MAPPER, ignoreFields);
    }

    @Override
    public JsonObject toJson(@NonNull ObjectMapper mapper, @NonNull Set<String> ignoreFields) {
        if (ignoreFields.isEmpty()) {
            return this.toJson(mapper);
        }
        JsonObject json = this.pojo.toJson();
        try {
            return mapper.readValue(mapper.writer(JsonData.ignoreFields(ignoreFields)).writeValueAsBytes(json.getMap()),
                                    JsonObject.class);
        } catch (IOException e) {
            LOGGER.warn("Cannot re-parse pojo {}", pojo.getClass(), e);
            return json;
        }
    }

}

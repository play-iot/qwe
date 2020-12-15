package io.github.zero88.msa.bp.dto;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.bp.dto.jackson.JsonModule;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JsonData {

    ObjectMapper MAPPER = Json.mapper.copy()
                                     .registerModule(new JavaTimeModule())
                                     .registerModule(JsonModule.BASIC)
                                     .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    ObjectMapper LENIENT_MAPPER = MAPPER.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    String SUCCESS_KEY = "data";
    String ERROR_KEY = "error";
    String FILTER_PROP_BY_NAME = "filter_by_name";

    /**
     * Check if given class is able to added in json
     *
     * @param clazz Given class
     * @return {@code true} if able to added
     * @see Json
     */
    static boolean isAbleHandler(@NonNull Class<?> clazz) {
        return ReflectionClass.isJavaLangObject(clazz) || ReflectionClass.assertDataType(clazz, Instant.class) ||
               ReflectionClass.assertDataType(clazz, Map.class) || ReflectionClass.assertDataType(clazz, List.class) ||
               ReflectionClass.assertDataType(clazz, JsonObject.class) ||
               ReflectionClass.assertDataType(clazz, JsonArray.class);
    }

    static Object checkAndConvert(@NonNull Object val) {
        return isAbleHandler(val.getClass()) ? val : val.toString();
    }

    static <D> D safeGet(@NonNull JsonObject jsonObject, @NonNull String key, @NonNull Class<D> clazz) {
        return safeGet(jsonObject, key, clazz, null);
    }

    @SuppressWarnings("unchecked")
    static <D> D safeGet(@NonNull JsonObject jsonObject, @NonNull String key, @NonNull Class<D> clazz, D defValue) {
        final Object value = jsonObject.getValue(key);
        if (Objects.isNull(value)) {
            return defValue;
        }
        if (ReflectionClass.assertDataType(value.getClass(), clazz)) {
            return clazz.cast(value);
        }
        if (ReflectionClass.assertDataType(clazz, JsonObject.class) &&
            ReflectionClass.assertDataType(value.getClass(), Map.class)) {
            return (D) JsonData.tryParse(clazz).toJson();
        }
        return defValue;
    }

    static <T> T convert(@NonNull JsonObject jsonObject, @NonNull Class<T> clazz) {
        return convert(jsonObject, clazz, MAPPER);
    }

    /**
     * Convert lenient with ignore unknown properties
     *
     * @param jsonObject json object
     * @param clazz      data type class
     * @param <T>        Expected Data Type
     * @return Expected instance
     * @throws IllegalArgumentException If conversion fails due to incompatible type
     */
    static <T> T convertLenient(@NonNull JsonObject jsonObject, @NonNull Class<T> clazz) {
        return convert(jsonObject, clazz, LENIENT_MAPPER);
    }

    static <T> T convert(@NonNull JsonObject jsonObject, @NonNull Class<T> clazz, @NonNull ObjectMapper mapper) {
        return mapper.convertValue(jsonObject.getMap(), clazz);
    }

    /**
     * Try parse {@code buffer} to {@code json data}
     *
     * @param buffer      Buffer data
     * @param isJson      Identify given {@code buffer} data is strictly {@code json object} or {@code json array}
     * @param useErrorKey Use whether {@link #ERROR_KEY} or {@link #SUCCESS_KEY} in case of fallback if given {@code
     *                    buffer} is not {@link JsonObject}
     * @return default {@code json data} instance
     */
    static JsonData tryParse(@NonNull Buffer buffer, boolean isJson, boolean useErrorKey) {
        return tryParse(buffer, isJson, useErrorKey ? ERROR_KEY : SUCCESS_KEY, false);
    }

    /**
     * Try parse {@code buffer} to {@code json data}
     *
     * @param buffer    Buffer data
     * @param isJson    Identify given {@code buffer} data is strictly {@code json object} or {@code json array}
     * @param backupKey Fallback key if given {@code buffer} is not {@link JsonObject}
     * @param isWrapped Whether output is needed to be wrapped or not
     * @return default {@code json data} instance
     */
    static JsonData tryParse(@NonNull Buffer buffer, boolean isJson, @NonNull String backupKey, boolean isWrapped) {
        return DefaultJsonData.tryParse(buffer, isJson, backupKey, isWrapped);
    }

    /**
     * Try parse {@code buffer} to {@code json data}
     *
     * @param buffer      Buffer data
     * @param isJson      Identify given {@code buffer} data is strictly {@code json object} or {@code json array}
     * @param useErrorKey Use whether {@link #ERROR_KEY} or {@link #SUCCESS_KEY} in case of fallback if given {@code
     *                    buffer} is not {@link JsonObject}
     * @param isWrapped   Whether output is needed to be wrapped or not
     * @return default {@code json data} instance
     */
    static JsonData tryParse(@NonNull Buffer buffer, boolean isJson, boolean useErrorKey, boolean isWrapped) {
        return tryParse(buffer, isJson, useErrorKey ? ERROR_KEY : SUCCESS_KEY, isWrapped);
    }

    /**
     * Try parse {@code buffer} to {@code json data} with {@link #SUCCESS_KEY}
     *
     * @param buffer Buffer data
     * @return default {@code json data} instance
     * @see JsonData#tryParse(Buffer, boolean, boolean, boolean)
     */
    static JsonData tryParse(@NonNull Buffer buffer) {
        return tryParse(buffer, false, false, false);
    }

    /**
     * Try parse {@code buffer} to {@code json data} with {@link #SUCCESS_KEY}
     *
     * @param buffer    Buffer data
     * @param isWrapped Whether output is needed to be wrapped or not
     * @return default {@code json data} instance
     * @see JsonData#tryParse(Buffer, boolean, boolean, boolean)
     */
    static JsonData tryParse(@NonNull Buffer buffer, boolean isWrapped) {
        return tryParse(buffer, false, false, isWrapped);
    }

    static JsonData tryParse(@NonNull Object obj) {
        return DefaultJsonData.tryParse(obj);
    }

    static <T extends JsonData> T from(Object object, Class<T> clazz) {
        return from(object, clazz, "Invalid data format");
    }

    static <T extends JsonData> T from(Object object, Class<T> clazz, String errorMsg) {
        return from(object, clazz, MAPPER, errorMsg);
    }

    static <T extends JsonData> T from(Object object, Class<T> clazz, ObjectMapper mapper) {
        return from(object, clazz, mapper, null);
    }

    static <T extends JsonData> T from(@NonNull Object object, @NonNull Class<T> clazz, @NonNull ObjectMapper mapper,
                                       String errorMsg) {
        try {
            JsonObject entries = SerializerFunction.builder().mapper(mapper).build().apply(object);
            return mapper.convertValue(entries.getMap(), clazz);
        } catch (IllegalArgumentException | NullPointerException | DecodeException ex) {
            //            final Throwable cause = JacksonExceptionPrettier.getCause(ex);
            //            throw BlueprintExceptionConverter.friendly(cause, errorMsg);
            throw new BlueprintException(ErrorCode.INVALID_ARGUMENT, errorMsg, new HiddenException(ex));
        }
    }

    static FilterProvider ignoreFields(Set<String> ignoreFields) {
        return new SimpleFilterProvider().addFilter(FILTER_PROP_BY_NAME,
                                                    SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields));
    }

    static <T extends JsonData> T merge(@NonNull JsonObject from, @NonNull JsonObject to, @NonNull Class<T> clazz) {
        return from(from.mergeIn(to, true), clazz);
    }

    default JsonObject toJson() {
        return toJson(getMapper());
    }

    default JsonObject toJson(@NonNull Set<String> ignoreFields) {
        return toJson(getMapper(), ignoreFields);
    }

    default JsonObject toJson(@NonNull ObjectMapper mapper) {
        return mapper.convertValue(this, JsonObject.class);
    }

    default JsonObject toJson(@NonNull ObjectMapper mapper, @NonNull Set<String> ignoreFields) {
        if (ignoreFields.isEmpty()) {
            return toJson(mapper);
        }
        return mapper.copy()
                     .addMixIn(JsonData.class, PropertyFilterMixIn.class)
                     .setFilterProvider(ignoreFields(ignoreFields))
                     .convertValue(this, JsonObject.class);
    }

    @JsonIgnore
    default ObjectMapper getMapper() { return MAPPER; }

    @JsonFilter(FILTER_PROP_BY_NAME)
    class PropertyFilterMixIn {}


    @NoArgsConstructor
    class DefaultJsonData extends HashMap<String, Object> implements JsonData {

        private static final Logger logger = LoggerFactory.getLogger(DefaultJsonData.class);

        DefaultJsonData(@NonNull Map<String, Object> map) { this.putAll(map); }

        DefaultJsonData(@NonNull JsonObject json)         { this(json.getMap()); }

        static JsonData tryParse(@NonNull Buffer buffer, boolean isJson, String backupKey, boolean isWrapper) {
            if (buffer.length() == 0) {
                return new DefaultJsonData();
            }
            try {
                if (isWrapper) {
                    return new DefaultJsonData(new JsonObject().put(backupKey, buffer.toJsonObject()));
                }
                return new DefaultJsonData(buffer.toJsonObject());
            } catch (DecodeException e) {
                logger.trace("Failed to parse json. Try json array", e);
                JsonObject data = new JsonObject();
                try {
                    data.put(backupKey, buffer.toJsonArray());
                } catch (DecodeException ex) {
                    if (isJson) {
                        throw new BlueprintException(ErrorCode.INVALID_ARGUMENT,
                                                     "Cannot parse json data. Received data: " + buffer.toString(), ex);
                    }
                    logger.trace("Failed to parse json array. Use text", ex);
                    //TODO check length, check encode
                    data.put(backupKey, buffer.toString());
                }
                return new DefaultJsonData(data);
            }
        }

        static JsonData tryParse(@NonNull Object obj) {
            if (obj instanceof JsonData) {
                return (JsonData) obj;
            }
            if (obj instanceof Buffer) {
                return tryParse((Buffer) obj, true, SUCCESS_KEY, false);
            }
            return new DefaultJsonData(
                SerializerFunction.builder().backupKey(SUCCESS_KEY).lenient(true).mapper(MAPPER).build().apply(obj));
        }

    }


    @Builder(builderClassName = "Builder")
    class SerializerFunction implements Function<Object, JsonObject> {

        private static final Logger logger = LoggerFactory.getLogger(SerializerFunction.class);

        @NonNull
        @Default
        private final String backupKey = "data";
        @Default
        private final boolean lenient = false;
        @NonNull
        private final ObjectMapper mapper;

        @SuppressWarnings("unchecked")
        @Override
        public JsonObject apply(Object obj) {
            if (obj instanceof String) {
                try {
                    return new JsonObject(mapper.readValue((String) obj, Map.class));
                } catch (IOException e) {
                    logger.trace("Failed mapping to json. Fallback to construct Json from plain string", e);
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
                return decode(new ArrayList((Collection) obj), "Failed to decode from Collection");
            }
            try {
                return new JsonObject((Map<String, Object>) mapper.convertValue(obj, Map.class));
            } catch (IllegalArgumentException e) {
                logger.trace("Failed mapping to json. Fallback to construct Json from plain object", e);
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

}


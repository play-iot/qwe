package io.github.zero88.qwe;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.utils.Configs;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Functions.Silencer;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Reflections.ReflectionField;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//TODO Should simplify
public interface IConfig extends JsonData, Shareable {

    ObjectMapper MAPPER = JsonData.MAPPER.copy().setSerializationInclusion(Include.NON_NULL);
    ObjectMapper MAPPER_IGNORE_UNKNOWN_PROPERTY = MAPPER.copy()
                                                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                                   false);

    static <T extends IConfig> T fromClasspath(String jsonFile, Class<T> clazz) {
        return IConfig.from(Configs.loadJsonConfig(jsonFile), clazz);
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz) {
        return from(data, clazz, "Invalid config format");
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz, String errorMsg) {
        return from(data, clazz, errorMsg, null);
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz, HiddenException cause) {
        return from(data, clazz, null, cause);
    }

    static <T extends IConfig> T from(@NonNull Object data, @NonNull Class<T> clazz, String errorMsg,
                                      HiddenException cause) {
        try {
            JsonObject entries = data instanceof String
                                 ? new JsonObject((String) data)
                                 : JsonObject.mapFrom(Objects.requireNonNull(data));
            return CreateConfig.create(clazz, entries, MAPPER);
        } catch (IllegalArgumentException | NullPointerException | DecodeException | HiddenException ex) {
            HiddenException hidden = ex instanceof HiddenException ? (HiddenException) ex : new HiddenException(ex);
            if (Objects.nonNull(cause)) {
                hidden.addSuppressed(Objects.nonNull(cause.getCause()) ? cause.getCause() : cause);
            }
            String msg = Strings.isNotBlank(errorMsg) ? errorMsg : "Invalid config format";
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, msg, hidden);
        }
    }

    static <T extends IConfig> T merge(@NonNull JsonObject from, @NonNull JsonObject to, @NonNull Class<T> clazz) {
        return from(from.mergeIn(to, true), clazz);
    }

    @SuppressWarnings("unchecked")
    static <T extends IConfig> T merge(@NonNull Object from, @NonNull Object to, @NonNull Class<T> clazz) {
        if (from instanceof JsonObject && to instanceof JsonObject) {
            return merge((JsonObject) from, (JsonObject) to, clazz);
        }
        if (clazz.isInstance(from) && clazz.isInstance(to)) {
            return ((T) from).merge((T) to);
        }
        if (clazz.isInstance(from) && to instanceof JsonObject) {
            return merge(((T) from).toJson(), to, clazz);
        }
        if (clazz.isInstance(from)) {
            return ((T) from).merge(from(to, clazz));
        }
        if (clazz.isInstance(to)) {
            return from(from, clazz).merge((T) to);
        }
        return from(from, clazz).merge(from(to, clazz));
    }

    static <C extends IConfig> C merge(JsonObject oldOne, JsonObject newOne, boolean isUpdated, Class<C> clazz) {
        if (Objects.isNull(newOne)) {
            return IConfig.from(oldOne, clazz);
        }
        if (isUpdated) {
            return IConfig.from(newOne, clazz);
        }
        JsonObject oldApp = IConfig.from(oldOne, clazz).toJson();
        JsonObject newApp = IConfig.from(newOne, clazz).toJson();
        return IConfig.merge(oldApp, newApp, clazz);
    }

    static <C extends IConfig> C parseConfig(JsonObject config, Class<C> clazz, Supplier<C> fallback) {
        try {
            return IConfig.from(config, clazz);
        } catch (CarlException ex) {
            return fallback.get();
        }
    }

    @JsonIgnore
    String key();

    @JsonIgnore
    Class<? extends IConfig> parent();

    @JsonIgnore
    default boolean isRoot() {
        return Objects.isNull(parent());
    }

    @SuppressWarnings("unchecked")
    default <T extends IConfig> T merge(@NonNull T to) {
        return (T) merge(toJson(), to.toJson(), getClass());
    }

    default <T extends IConfig> JsonObject mergeToJson(@NonNull T to) {
        return this.toJson().mergeIn(to.toJson(), true);
    }

    @Override
    default JsonObject toJson() {
        List<? extends IConfig> fieldValues = ReflectionField.getFieldValuesByType(this, IConfig.class);
        JsonObject jsonObject = getMapper().convertValue(this, JsonObject.class);
        fieldValues.forEach(val -> jsonObject.put(val.key(), val.toJson()));
        return jsonObject;
    }

    default ObjectMapper getMapper() {
        return MAPPER;
    }

    @Override
    default IConfig copy() {
        return IConfig.from(toJson().getMap(), this.getClass());
    }

    @RequiredArgsConstructor
    class CreateConfig<T extends IConfig> extends Silencer<T> {

        private final Class<T> clazz;
        private final JsonObject entries;
        private final ObjectMapper mapper;

        static <T extends IConfig> T create(Class<T> clazz, JsonObject data, ObjectMapper mapper) {
            final T temp = Functions.getOrDefault(() -> mapper.convertValue(data, clazz),
                                                  () -> ReflectionClass.createObject(clazz));
            final CreateConfig<T> creator = new CreateConfig<>(clazz, data, mapper);
            creator.accept(temp, null);
            return creator.get();
        }

        @Override
        public void accept(T temp, HiddenException throwable) {
            if (Objects.nonNull(throwable)) {
                throw new HiddenException(throwable.getCause());
            }
            try {
                object = create(temp.key(), entries, clazz);
            } catch (HiddenException ex) {
                if (temp.isRoot()) {
                    throw ex;
                }
                IConfig parent = from(entries, temp.parent(), ex);
                JsonObject parentValue = parent instanceof Map && ((Map) parent).containsKey(parent.key())
                                         ? parent.toJson().getJsonObject(parent.key(), new JsonObject())
                                         : parent.toJson();
                Object currentValue = parentValue.getValue(temp.key());
                if (Objects.isNull(currentValue)) {
                    throw ex;
                }
                object = from(currentValue, clazz);
            }
        }

        private T create(String name, JsonObject entries, Class<T> clazz) {
            try {
                JsonObject values = Strings.isNotBlank(name) && entries.containsKey(name)
                                    ? entries.getJsonObject(name)
                                    : entries;
                return mapper.convertValue(values.getMap(), clazz);
            } catch (IllegalArgumentException | ClassCastException e) {
                throw new HiddenException(ErrorCode.INVALID_ARGUMENT, "Jackson cannot convert data", e);
            }
        }

    }

}

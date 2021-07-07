package io.zero88.qwe;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.repl.ReflectionField;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Functions.Provider;
import io.github.zero88.utils.Functions.Silencer;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.exceptions.ConfigException;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.utils.JsonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface IConfig extends JsonData, Shareable {

    ObjectMapper MAPPER = JsonData.MAPPER.copy().setSerializationInclusion(Include.NON_NULL);
    ObjectMapper MAPPER_IGNORE_UNKNOWN_PROPERTY = MAPPER.copy()
                                                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                                   false);

    static <T extends IConfig> T fromClasspath(String jsonFile, Class<T> clazz) {
        return IConfig.from(JsonUtils.loadJsonInClasspath(jsonFile), clazz);
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz) {
        return from(data, clazz, null, null);
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz, String errorMsg) {
        return from(data, clazz, errorMsg, null);
    }

    static <T extends IConfig> T from(Object data, Class<T> clazz, HiddenException cause) {
        return from(data, clazz, null, cause);
    }

    static <T extends IConfig> T from(Object data, @NonNull Class<T> clazz, String errorMsg, HiddenException cause) {
        try {
            JsonObject cfg;
            if (data instanceof String) {
                cfg = new JsonObject((String) data);
            } else if (data instanceof IConfig) {
                cfg = ((IConfig) data).toJson();
            } else if (data instanceof JsonObject) {
                cfg = (JsonObject) data;
            } else {
                cfg = JsonObject.mapFrom(Objects.requireNonNull(data, "Config data is null"));
            }
            return CreateConfig.create(clazz, cfg, MAPPER);
        } catch (IllegalArgumentException | NullPointerException | DecodeException | HiddenException ex) {
            Throwable c = ex instanceof HiddenException ? ex.getCause() : ex;
            if (Objects.nonNull(cause)) {
                c.addSuppressed(Objects.nonNull(cause.getCause()) ? cause.getCause() : cause);
            }
            throw new ConfigException(errorMsg, c);
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

    static <C extends IConfig> C parseConfig(JsonObject config, Class<C> clazz, Supplier<C> fallback) {
        try {
            return IConfig.from(config, clazz);
        } catch (QWEException ex) {
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

    @Override
    default JsonObject toJson(@NonNull ObjectMapper mapper) {
        List<? extends IConfig> fieldValues = ReflectionField.getFieldValuesByType(this, IConfig.class);
        JsonObject jsonObject = mapper.convertValue(this, JsonObject.class);
        fieldValues.forEach(val -> jsonObject.put(val.key(), val.toJson(mapper)));
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
        private final static Logger log = LoggerFactory.getLogger(IConfig.class);

        static <T extends IConfig> T create(Class<T> clazz, JsonObject data, ObjectMapper mapper) {
            final Provider<T> p = () -> ReflectionClass.createObject(clazz);
            final T temp = Functions.getIfThrow(t -> log.trace("Cannot init " + clazz, t), p)
                                    .orElseGet(() -> mapper.convertValue(data, clazz));
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
            } catch (ClassCastException e) {
                throw new HiddenException("Entry [" + name + "] is not json format", e);
            } catch (IllegalArgumentException e) {
                throw new HiddenException("Entry [" + name + "] is unable to convert", e);
            }
        }

    }

}

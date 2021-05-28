package io.zero88.qwe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
interface IOtherConfig<C extends IOtherConfig> extends IConfig {

    /**
     * Return view of other configuration
     *
     * @return configuration
     */
    @NotNull JsonObject other();

    /**
     * Lookup a particular configuration by config key
     *
     * @param key config key
     * @return a particular configuration
     */
    @Nullable Object lookup(String key);

    /**
     * Lookup a particular configuration in specific type by config key
     *
     * @param key         config key
     * @param configClass config class
     * @param <T>         Type of configuration
     * @return a particular configuration
     */
    @Nullable <T> T lookup(@NonNull String key, @NonNull Class<T> configClass);

    C put(@NotNull String configKey, Object config);

    C putAll(Map<String, Object> other);

    @SuppressWarnings("unchecked")
    abstract class HasOtherConfig<C extends IOtherConfig> implements IOtherConfig<C> {

        @JsonIgnore
        protected final Map<String, Object> other;

        protected HasOtherConfig() {
            this(null);
        }

        protected HasOtherConfig(Map<String, Object> other) {
            this.other = Optional.ofNullable(other).orElseGet(HashMap::new);
        }

        public JsonObject other()        { return JsonObject.mapFrom(other); }

        public Object lookup(String key) { return other.get(key); }

        public <T> T lookup(String key, @NonNull Class<T> configClass) {
            return Optional.ofNullable(other.get(key))
                           .map(o -> Functions.getOrDefault((T) null, () -> configClass.cast(o)))
                           .orElse(null);
        }

        @Override
        public C put(@NotNull String configKey, Object config) {
            if (Strings.isBlank(configKey)) {
                throw new IllegalArgumentException("Config key must be not empty");
            }
            this.other.put(configKey, config);
            return (C) this;
        }

        @Override
        public C putAll(Map<String, Object> other) {
            if (other != null) {
                this.other.putAll(other);
            }
            return (C) this;
        }

        @Override
        public JsonObject toJson(@NonNull ObjectMapper mapper) {
            return mapper.convertValue(other, JsonObject.class);
        }

    }

}

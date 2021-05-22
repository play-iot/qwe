package io.zero88.qwe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.zero88.utils.Functions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;

/**
 * An {@code Application} configuration can contains itself configuration and its {@code Component} configurations
 *
 * @see Application
 * @see Component
 */
public final class QWEAppConfig implements IConfig {

    public static final String NAME = "__app__";
    public static final String DELIVERY_OPTIONS = "__delivery__";

    /**
     * EventBus delivery options
     */
    @Getter
    @JsonIgnore
    private final DeliveryOptions deliveryOptions;
    /**
     * Other Application configurations or Application component configuration
     */
    @JsonIgnore
    private final Map<String, Object> other;

    public QWEAppConfig() {
        this.other = new HashMap<>();
        this.deliveryOptions = new DeliveryOptions();
    }

    @JsonCreator
    public QWEAppConfig(Map<String, Object> map) {
        Map<String, Object> m = Optional.ofNullable(map).orElseGet(HashMap::new);
        this.deliveryOptions = Optional.ofNullable(m.remove(QWEAppConfig.DELIVERY_OPTIONS))
                                       .map(o -> new DeliveryOptions(JsonObject.mapFrom(o)))
                                       .orElseGet(DeliveryOptions::new);
        this.other = m;
    }

    @Override
    public String key() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return QWEConfig.class; }

    @Override
    public JsonObject toJson(ObjectMapper mapper) {
        return mapper.convertValue(other, JsonObject.class).put(DELIVERY_OPTIONS, this.deliveryOptions.toJson());
    }

    public Object lookup(String key) { return other.get(key); }

    public <T> T lookup(String key, @NonNull Class<T> configClass) {
        return Optional.ofNullable(other.get(key))
                       .map(o -> Functions.getOrDefault((T) null, () -> configClass.cast(o)))
                       .orElse(null);
    }

    public JsonObject other() { return JsonObject.mapFrom(other); }

}

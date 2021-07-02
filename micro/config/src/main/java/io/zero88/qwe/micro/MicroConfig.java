package io.zero88.qwe.micro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.zero88.qwe.ComponentConfig.DynamicComponentConfig;
import io.zero88.qwe.IOtherConfig.HasOtherConfig;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class MicroConfig extends HasOtherConfig<MicroConfig> implements DynamicComponentConfig<MicroConfig> {

    public static final String NAME = "__micro__";

    @Override
    public String key() { return NAME; }

    public MicroConfig() {
        this(null);
    }

    @JsonCreator
    public MicroConfig(Map<String, Object> map) {
        this.other.putAll(find(Optional.ofNullable(map).orElseGet(HashMap::new)));
    }

}

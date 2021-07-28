package io.zero88.qwe.micro;

import java.util.Map;

import io.zero88.qwe.PluginConfig.DynamicPluginConfig.DynamicPluginConfigImpl;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class MicroConfig extends DynamicPluginConfigImpl<MicroConfig> {

    public static final String NAME = "__micro__";

    @Override
    public String configKey() { return KEY; }

    public MicroConfig() {
        this(null);
    }

    @JsonCreator
    public MicroConfig(Map<String, Object> map) {
        super(map);
    }

}

package cloud.playio.qwe.micro;

import java.util.Map;

import cloud.playio.qwe.PluginConfig.DynamicPluginConfig.DynamicPluginConfigImpl;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class MicroConfig extends DynamicPluginConfigImpl<MicroConfig> {

    public static final String KEY = "__micro__";

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

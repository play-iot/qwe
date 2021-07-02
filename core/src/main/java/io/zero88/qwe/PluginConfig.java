package io.zero88.qwe;

import java.util.Map;

public interface PluginConfig extends IConfig {

    String PLUGIN_DEPLOY_CONFIG_KEY = "__deployment__";

    @Override
    default Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
    }

    /**
     * Defines a particular deployment options for plugin.
     * <p>
     * If a {@code plugin} want to declare its deployment options, the config must be used this key and attach under
     * {@link QWEAppConfig}
     *
     * @return particular
     */
    default String deploymentKey() {
        return PLUGIN_DEPLOY_CONFIG_KEY + key();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    interface DynamicPluginConfig<C extends DynamicPluginConfig> extends PluginConfig, IOtherConfig<C> {

        default Map<String, Object> find(Map<String, Object> map) {
            if (map.containsKey(QWEAppConfig.NAME)) {
                return find((Map<String, Object>) map.get(QWEAppConfig.NAME));
            }
            if (map.containsKey(key())) {
                return (Map<String, Object>) map.get(key());
            }
            return map;
        }

    }

}

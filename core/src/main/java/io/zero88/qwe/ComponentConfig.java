package io.zero88.qwe;

import java.util.Map;

public interface ComponentConfig extends IConfig {

    String COMP_DEPLOY_CONFIG_KEY = "__deployment__";

    @Override
    default Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
    }

    /**
     * Defines a particular deployment options for component.
     * <p>
     * If a {@code component} want to declare its deployment options, the config must be used this key and attach under
     * {@link QWEAppConfig}
     *
     * @return particular
     */
    default String deploymentKey() {
        return COMP_DEPLOY_CONFIG_KEY + key();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    interface DynamicComponentConfig<C extends DynamicComponentConfig> extends ComponentConfig, IOtherConfig<C> {

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

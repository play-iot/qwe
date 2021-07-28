package io.zero88.qwe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

/**
 * A {@code Plugin} configuration
 *
 * @see Plugin
 */
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
        return PLUGIN_DEPLOY_CONFIG_KEY + configKey();
    }

    /**
     * If a {@code Plugin} need to use local data storage then use this interface for its configuration.
     *
     * @see PluginConfig
     */
    interface PluginDirConfig extends PluginConfig {

        /**
         * Defines plugin dir configuration
         * <p>
         * If a relative path, then the actual plugin path will be child folder of {@code application data dir}.
         *
         * @return plugin dir
         */
        @Nullable String getPluginDir();

    }


    /**
     * If a {@code Plugin} defines a domain configuration that contains many configuration part.
     *
     * @param <C> Type of DynamicPluginConfig
     * @see IOtherConfig
     * @see PluginConfig
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    interface DynamicPluginConfig<C extends DynamicPluginConfig> extends PluginConfig, IOtherConfig<C> {

        /**
         * Find correct plugin config map
         *
         * @param map json data
         * @return plugin config
         */
        default Map<String, Object> find(Map<String, Object> map) {
            if (map.containsKey(QWEAppConfig.KEY)) {
                return find((Map<String, Object>) map.get(QWEAppConfig.KEY));
            }
            if (map.containsKey(configKey())) {
                return (Map<String, Object>) map.get(configKey());
            }
            return map;
        }

        abstract class DynamicPluginConfigImpl<C extends DynamicPluginConfig> extends HasOtherConfig<C>
            implements DynamicPluginConfig<C> {

            public DynamicPluginConfigImpl(Map<String, Object> other) {
                super();
                this.putAll(find(Optional.ofNullable(other).orElseGet(HashMap::new)));
            }

        }

    }

}

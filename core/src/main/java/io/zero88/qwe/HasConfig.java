package io.zero88.qwe;

import org.slf4j.Logger;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.utils.Configs;

import lombok.NonNull;

/**
 * Mark a verticle has Config
 *
 * @param <C> type of {@code IConfig}
 * @see IConfig
 */
interface HasConfig<C extends IConfig> {

    /**
     * Config class
     *
     * @return IConfig class
     */
    @NonNull Class<C> configClass();

    /**
     * Define a config file in classpath
     *
     * @return config file path
     */
    @NonNull String configFile();

    /**
     * Compute configure based on user input configuration and default unit configuration that defined in {@link
     * #configFile()}
     *
     * @param logger logger
     * @param config given user configuration
     * @return config instance
     * @see IConfig
     */
    default C computeConfig(Logger logger, JsonObject config) {
        logger.debug("Computing configuration [{}][{}]", configClass().getName(), configFile());
        C cfg = IConfig.merge(IConfig.from(Configs.silentLoadJsonConfig(configFile()), configClass()), config,
                              configClass());
        if (logger.isDebugEnabled()) {
            logger.debug("Configuration [{}][{}]", getClass().getName(), cfg.toJson().encode());
        }
        return cfg;
    }

}

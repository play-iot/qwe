package io.zero88.qwe.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.Router;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public interface RouterCreator<T extends RouterConfig> extends HasLogger, HttpSystem {

    default Logger logger() {
        return LoggerFactory.getLogger(RouterCreator.class);
    }

    /**
     * Mount sub router into root router
     *
     * @param rootRouter root router
     * @param config     router config
     * @param sharedData shared data
     * @return a reference to root router for fluent API
     */
    default @NonNull Router mount(@NonNull Router rootRouter, @NonNull T config,
                                  @NonNull SharedDataLocalProxy sharedData) {
        if (!config.isEnabled() || !validate(config)) {
            return rootRouter;
        }
        logger().info(decor("Setup route [{}][{}]"), routerName(), BasePaths.addWildcards(config.getPath()));
        rootRouter.mountSubRouter(mountPoint(config), subRouter(config, sharedData));
        return rootRouter;
    }

    default boolean validate(T config) {
        return true;
    }

    default String routerName() {
        return function();
    }

    default @NonNull String mountPoint(@NonNull T config) {
        return config.getPath();
    }

    /**
     * Create new sub router based on configuration. It will be mounted as sub router in root router
     *
     * @param config     Router config
     * @param sharedData Shared data
     * @return router
     * @see #mount(Router, RouterConfig, SharedDataLocalProxy)
     */
    @NonNull Router subRouter(@NonNull T config, @NonNull SharedDataLocalProxy sharedData);

}

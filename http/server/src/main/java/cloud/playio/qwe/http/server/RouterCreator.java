package cloud.playio.qwe.http.server;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Urls;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public interface RouterCreator<C extends RouterConfig> extends RouterBuilder, HasLogger, HttpSystem {

    static Route addContentType(Route route, List<String> contentTypes) {
        if (contentTypes == null || contentTypes.isEmpty()) {
            return route;
        }
        return contentTypes.stream().reduce(route, Route::produces, (r1, r2) -> r1);
    }

    static String addWildcards(String path) {
        return Urls.combinePath(path, "*");
    }

    default Logger logger() {
        return LoggerFactory.getLogger(RouterCreator.class);
    }

    @Override
    default Router setup(Vertx vertx, Router rootRouter, HttpServerConfig config, HttpServerPluginContext context) {
        C cfg = lookupConfig().apply(config);
        if (!cfg.isEnabled() || !validate(cfg)) {
            return rootRouter;
        }
        logger().info("Setup router [{}][{}]", routerName(), addWildcards(routePath(cfg)));
        rootRouter.mountSubRouter(mountPoint(cfg), subRouter(context.sharedData(),
                                                             Objects.requireNonNull(context.dataDir(),
                                                                                    "Missing HTTP plugin dir"), cfg));
        return rootRouter;
    }

    default boolean validate(C config) {
        return true;
    }

    default String routerName() {
        return function();
    }

    default @NonNull
    String mountPoint(@NonNull C config) {
        return config.getPath();
    }

    default String routePath(@NonNull C config) {
        return mountPoint(config);
    }

    default Route createRoute(Router router, C config, RoutePath routePath) {
        return createRoute(router, config, routePath, true);
    }

    default Route createRoute(Router router, C config, RoutePath routePath, boolean enableLog) {
        if (enableLog) {
            logger().info(decor("Register route [{}::{}]"), routePath.getMethod(),
                          Urls.combinePath(mountPoint(config), routePath.getPath()));
        }
        return addContentType(router.route(routePath.getMethod(), routePath.getPath()), routePath.getContentTypes());
    }

    Function<HttpServerConfig, C> lookupConfig();

    /**
     * Create new sub router based on configuration. It will be mounted as sub router in root router
     *
     * @param sharedData Shared data proxy
     * @param pluginDir  the plugin dir
     * @param config     Router config
     * @return the sub router
     * @see RouterBuilder#setup(Vertx, Router, HttpServerConfig, HttpServerPluginContext)
     */
    @NonNull
    Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir, @NonNull C config);

}

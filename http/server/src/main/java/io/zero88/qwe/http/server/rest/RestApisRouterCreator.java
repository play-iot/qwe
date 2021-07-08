package io.zero88.qwe.http.server.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.repl.Reflections;
import io.github.zero88.utils.TripleFunction;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.zero88.qwe.http.server.rest.api.RestApi;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.micro.DiscoveryContext;

import lombok.NonNull;

/**
 * @see RouterCreator
 */
public final class RestApisRouterCreator implements ApisCreator<ApiConfig> {

    @NonNull
    private final Set<Class<? extends RestApi>> restApiClass = new HashSet<>();
    @NonNull
    private final Set<Class<? extends RestEventApi>> restEventApiClass = new HashSet<>();

    public RestApisRouterCreator registerApi(Collection<Class<? extends RestApi>> apiClass) {
        restApiClass.addAll(apiClass);
        return this;
    }

    public RestApisRouterCreator registerEventBusApi(Collection<Class<? extends RestEventApi>> eventBusApiClasses) {
        restEventApiClass.addAll(eventBusApiClasses);
        return this;
    }

    @Override
    public @NonNull Router router(@NonNull ApiConfig config, @NonNull SharedDataLocalProxy sharedData) {
        if (restApiClass.isEmpty() && restEventApiClass.isEmpty() && !config.getDynamicConfig().isEnabled()) {
            throw new InitializerError("No REST API given, register at least one.");
        }
        Router mainRouter = Router.router(sharedData.getVertx());
        this.addSubRouter(mainRouter, config, sharedData, this::initRestApiRouter)
            .addSubRouter(mainRouter, config, sharedData, this::initEventBusApiRouter)
            .addSubRouter(mainRouter, config, sharedData, this::initDynamicRouter);
        HttpServerPlugin.restrictJsonRoute(
            mainRouter.route(BasePaths.addWildcards(config.path())).handler(new EventMessageResponseHandler()));
        return mainRouter;
    }

    private RestApisRouterCreator addSubRouter(Router mainRouter, ApiConfig config,
                                               @NonNull SharedDataLocalProxy sharedData,
                                               TripleFunction<Router, ApiConfig, SharedDataLocalProxy, Router> provider) {
        final Router subRouter = provider.accept(mainRouter, config, sharedData);
        if (Objects.nonNull(subRouter)) {
            mainRouter.mountSubRouter(config.getPath(), subRouter);
        }
        return this;
    }

    private Router initRestApiRouter(Router mainRouter, @NonNull ApiConfig config,
                                     @NonNull SharedDataLocalProxy sharedData) {
        if (restApiClass.isEmpty()) {
            return null;
        }
        Object[] classes = restApiClass.toArray(new Class[] {});
        logger().info(decor("Registering sub router REST API..."));
        //TODO register RestAPI
        return null;
    }

    private Router initEventBusApiRouter(Router router, @NonNull ApiConfig config,
                                         @NonNull SharedDataLocalProxy sharedData) {
        if (restEventApiClass.isEmpty()) {
            return null;
        }
        return new RestEventApisCreator<ApiConfig>().register(restEventApiClass).mount(router, config, sharedData);
    }

    private Router initDynamicRouter(Router mainRouter, @NonNull ApiConfig config,
                                     @NonNull SharedDataLocalProxy sharedData) {
        if (!config.getDynamicConfig().isEnabled()) {
            return null;
        }
        try {
            Class.forName(DiscoveryContext.class.getName(), false, Reflections.contextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new InitializerError(
                "To enabled dynamic route, you have to put on qwe-micro-discovery.jar in classpath", e);
        }
        String path = BasePaths.addWildcards(config.getDynamicConfig().getPath());
        logger().info(decor("Registering sub router REST Dynamic API '{}' in disable mode..."), path);
        Router dynamicRouter = Router.router(sharedData.getVertx());
        dynamicRouter.route(path).disable();
        return dynamicRouter;
    }

}

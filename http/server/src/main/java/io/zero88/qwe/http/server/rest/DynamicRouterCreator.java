package io.zero88.qwe.http.server.rest;

import io.github.zero88.repl.Reflections;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.ApiDynamicRouteConfig;
import io.zero88.qwe.micro.DiscoveryContext;

import lombok.NonNull;

public final class DynamicRouterCreator implements RouterCreator<ApiDynamicRouteConfig>, ApisSystem {

    @Override
    public String function() {
        return "DynamicAPI";
    }

    @Override
    public @NonNull Router subRouter(@NonNull ApiDynamicRouteConfig config, @NonNull SharedDataLocalProxy sharedData) {
        try {
            Class.forName(DiscoveryContext.class.getName(), false, Reflections.contextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new InitializerError(
                "To enabled dynamic route, you have to put on qwe-micro-discovery.jar in classpath", e);
        }
        Router dynamicRouter = Router.router(sharedData.getVertx());
        dynamicRouter.route(BasePaths.addWildcards(config.getPath())).disable();
        return dynamicRouter;
    }

}

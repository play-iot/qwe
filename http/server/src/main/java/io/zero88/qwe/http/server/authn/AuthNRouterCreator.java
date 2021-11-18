package io.zero88.qwe.http.server.authn;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.http.EventHttpService;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.EventMethodMapping;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpSystem.AuthNSystem;
import io.zero88.qwe.http.server.RoutePath;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.AuthNConfig;
import io.zero88.qwe.http.server.handler.HttpEBDispatcher;

import lombok.NonNull;

public final class AuthNRouterCreator implements RouterCreator<AuthNConfig>, AuthNSystem {

    @Override
    public Function<HttpServerConfig, AuthNConfig> lookupConfig() {
        return HttpServerConfig::getAuthNConfig;
    }

    @Override
    public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                                     @NonNull AuthNConfig config) {
        final Router router = Router.router(sharedData.getVertx());
        config.getLoginListenerClasses()
              .stream()
              .map(ReflectionClass::findClass)
              .map(LoginListener::create)
              .filter(Objects::nonNull)
              .forEach(listener -> createRoute(sharedData, config, router, listener));
        return router;
    }

    private void createRoute(SharedDataLocalProxy sharedData, AuthNConfig config, Router router,
                             EventHttpService service) {
        EventBusClient.create(sharedData).register(service.address(), service);
        for (EventMethodDefinition definition : service.definitions()) {
            for (EventMethodMapping mapping : definition.getMapping()) {
                HttpEBDispatcher dispatcher = HttpEBDispatcher.create(sharedData.sharedKey(),
                                                                      new DeliveryEvent().address(service.address())
                                                                                         .action(mapping.getAction()));
                this.createRoute(router, config, RoutePath.create(mapping))
                    /*.handler(LoginHandler.create(config.getHandlerClass()).setup(dispatcher))*/;
            }
        }
    }

}

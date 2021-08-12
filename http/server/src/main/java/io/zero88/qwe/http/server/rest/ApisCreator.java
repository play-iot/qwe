package io.zero88.qwe.http.server.rest;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpRuntimeConfig;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.HttpServerPluginContext;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;
import io.zero88.qwe.micro.httpevent.EventMethodDefinition;
import io.zero88.qwe.micro.httpevent.EventMethodMapping;
import io.zero88.qwe.micro.httpevent.RestEventApiMetadata;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public abstract class ApisCreator<X, T extends RouterConfig> implements RouterCreator<T>, ApisSystem {

    @Getter(value = AccessLevel.PROTECTED)
    private final Set<Class<? extends X>> apis = new HashSet<>();

    @Override
    public Router setup(Vertx vertx, Router rootRouter, HttpServerConfig config, HttpServerPluginContext context) {
        register(config.getRuntimeConfig());
        return RouterCreator.super.setup(vertx, rootRouter, config, context);
    }

    @Override
    public String routerName() {
        return subFunction();
    }

    @Override
    public boolean validate(T config) {
        return !apis.isEmpty();
    }

    protected final ApisCreator<X, T> register(@NonNull Collection<Class<? extends X>> restApis) {
        restApis.stream().filter(Objects::nonNull).forEach(apis::add);
        return this;
    }

    protected abstract String subFunction();

    protected abstract void register(HttpRuntimeConfig runtimeConfig);

    public abstract static class RestEventApisCreatorImpl<C extends RouterConfig> extends ApisCreator<RestEventApi, C> {

        @Override
        public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                                         @NonNull C config) {
            Router router = Router.router(sharedData.getVertx());
            getApis().stream()
                     .map(ReflectionClass::createObject)
                     .filter(Objects::nonNull)
                     .forEach(api -> createRouter(router, api, config, sharedData));
            router.route(BasePaths.addWildcards(config.getPath()));
            return router;
        }

        protected void createRouter(Router router, RestEventApi restApi, C config, SharedDataLocalProxy sharedData) {
            restApi.initRouter(sharedData)
                   .getRestMetadata()
                   .forEach(metadata -> createRouter(router, config, metadata, restApi, sharedData));
        }

        protected void createRouter(Router router, C config, RestEventApiMetadata metadata, RestEventApi api,
                                    SharedDataLocalProxy sharedData) {
            final EventMethodDefinition definition = metadata.getDefinition();
            final EventBusClient client = EventBusClient.create(sharedData);
            for (EventMethodMapping mapping : definition.getMapping()) {
                String path = Strings.isBlank(mapping.getCapturePath())
                              ? definition.getServicePath()
                              : mapping.getCapturePath();
                logger().info(decor("Bind Path [{}::{}] to [{}::{}]"), Strings.padLeft(mapping.method(), 6),
                              Urls.combinePath(config.getPath(), path), metadata.getAddress(), mapping.getAction());
                HttpServerPlugin.restrictJsonRoute(router.route(mapping.getMethod(), path))
                                .order(definition.getOrder())
                                .handler(RestEventApiDispatcher.create(api.dispatcher(), client, metadata.getAddress(),
                                                                       mapping.getAction(), metadata.getPattern(),
                                                                       definition.isUseRequestData()))
                                .handler(new EventMessageResponseHandler());
            }
        }

    }

}

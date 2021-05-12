package io.zero88.qwe.http.server.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.event.RestEventApiMetadata;
import io.zero88.qwe.http.server.HttpServer;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;
import io.zero88.qwe.micro.http.EventMethodDefinition;
import io.zero88.qwe.micro.http.EventMethodMapping;

import lombok.NonNull;

/**
 * @see RouterCreator
 */
//TODO Refactor it as RouterCreator
public final class RestEventApisCreator implements ApisCreator {

    private final Vertx vertx;
    private final Router router;
    private final Set<Class<? extends RestEventApi>> apis = new HashSet<>();
    private SharedDataLocalProxy proxy;

    /**
     * For test
     */
    RestEventApisCreator() {
        this.vertx = null;
        this.router = null;
    }

    public RestEventApisCreator(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
    }

    public RestEventApisCreator addSharedDataProxy(@NonNull SharedDataLocalProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public RestEventApisCreator register(@NonNull Class<? extends RestEventApi> restApi) {
        apis.add(restApi);
        return this;
    }

    @SafeVarargs
    public final RestEventApisCreator register(Class<? extends RestEventApi>... restApi) {
        return this.register(Arrays.asList(restApi));
    }

    public RestEventApisCreator register(@NonNull Collection<Class<? extends RestEventApi>> restApis) {
        restApis.stream().filter(Objects::nonNull).forEach(apis::add);
        return this;
    }

    public Router build() {
        validate().stream().map(ReflectionClass::createObject).filter(Objects::nonNull).forEach(this::createRouter);
        return router;
    }

    Set<Class<? extends RestEventApi>> validate() {
        if (apis.isEmpty()) {
            throw new InitializerError("No REST API given, register at least one.");
        }
        return apis;
    }

    private void createRouter(RestEventApi restApi) {
        restApi.registerProxy(proxy)
               .initRouter()
               .getRestMetadata()
               .forEach(metadata -> this.createRouter(metadata, restApi));
    }

    private void createRouter(RestEventApiMetadata metadata, RestEventApi api) {
        final EventMethodDefinition definition = metadata.getDefinition();
        final EventBusClient eventbus = EventBusClient.create(proxy);
        for (EventMethodMapping mapping : definition.getMapping()) {
            RestEventApiDispatcher restHandler = RestEventApiDispatcher.create(api.dispatcher(), eventbus,
                                                                               metadata.getAddress(),
                                                                               mapping.getAction(),
                                                                               metadata.getPattern(),
                                                                               definition.isUseRequestData());
            final String path = Strings.isBlank(mapping.getCapturePath())
                                ? definition.getServicePath()
                                : mapping.getCapturePath();
            final String format = "Path:{}::{} --- Event:{}::{}::{}";
            log().info(decor("Registering route: " + format), mapping.getMethod(), path, metadata.getAddress(),
                       mapping.getAction(), metadata.getPattern());
            HttpServer.restrictJsonRoute(
                router.route(mapping.getMethod(), path).order(definition.getOrder()).handler(restHandler));
        }
    }

}

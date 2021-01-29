package io.github.zero88.qwe.http.server.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.http.event.RestEventApiMetadata;
import io.github.zero88.qwe.http.server.HttpServer;
import io.github.zero88.qwe.http.server.RouterCreator;
import io.github.zero88.qwe.http.server.rest.api.RestEventApi;
import io.github.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;
import io.github.zero88.qwe.micro.http.EventMethodDefinition;
import io.github.zero88.qwe.micro.http.EventMethodMapping;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @see RouterCreator
 */
@Slf4j
//TODO Refactor it as RouterCreator
public final class RestEventApisCreator {

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
        final EventbusClient eventbus = EventbusClient.create(proxy);
        for (EventMethodMapping mapping : definition.getMapping()) {
            RestEventApiDispatcher restHandler = RestEventApiDispatcher.create(api.dispatcher(), eventbus,
                                                                               metadata.getAddress(),
                                                                               mapping.getAction(),
                                                                               metadata.getPattern(),
                                                                               definition.isUseRequestData());
            final String path = Strings.isBlank(mapping.getCapturePath())
                                ? definition.getServicePath()
                                : mapping.getCapturePath();
            log.info("Registering route | Event Binding:\t{} {} --- {} {} {}", mapping.getMethod(), path,
                     metadata.getPattern(), mapping.getAction(), metadata.getAddress());
            HttpServer.restrictJsonRoute(
                router.route(mapping.getMethod(), path).order(definition.getOrder()).handler(restHandler));
        }
    }

}

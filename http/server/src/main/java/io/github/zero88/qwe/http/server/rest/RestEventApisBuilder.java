package io.github.zero88.qwe.http.server.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.component.SharedDataDelegate;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.http.event.RestEventApiMetadata;
import io.github.zero88.qwe.http.server.HttpServer;
import io.github.zero88.qwe.http.server.handler.RestEventApiDispatcher;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;
import io.github.zero88.qwe.micro.metadata.EventMethodMapping;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import lombok.NonNull;

public final class RestEventApisBuilder {

    private final Logger logger = LoggerFactory.getLogger(RestEventApisBuilder.class);
    private final Router router;
    private final Set<Class<? extends RestEventApi>> apis = new HashSet<>();
    private Function<String, Object> sharedDataFunc;

    /**
     * For test
     */
    RestEventApisBuilder() {
        this.router = null;
    }

    public RestEventApisBuilder(Vertx vertx) {
        this.router = Router.router(vertx);
    }

    public RestEventApisBuilder addSharedDataFunc(@NonNull Function<String, Object> func) {
        this.sharedDataFunc = func;
        return this;
    }

    public RestEventApisBuilder register(@NonNull Class<? extends RestEventApi> restApi) {
        apis.add(restApi);
        return this;
    }

    @SafeVarargs
    public final RestEventApisBuilder register(Class<? extends RestEventApi>... restApi) {
        return this.register(Arrays.asList(restApi));
    }

    public RestEventApisBuilder register(@NonNull Collection<Class<? extends RestEventApi>> restApis) {
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
        restApi.registerSharedData(sharedDataFunc)
               .initRouter()
               .getRestMetadata()
               .forEach(metadata -> this.createRouter(metadata, restApi));
    }

    private void createRouter(RestEventApiMetadata metadata, RestEventApi api) {
        final EventMethodDefinition definition = metadata.getDefinition();
        EventbusClient controller = (EventbusClient) sharedDataFunc.apply(SharedDataDelegate.SHARED_EVENTBUS);
        for (EventMethodMapping mapping : definition.getMapping()) {
            RestEventApiDispatcher restHandler = RestEventApiDispatcher.create(api.dispatcher(), controller,
                                                                               metadata.getAddress(),
                                                                               mapping.getAction(),
                                                                               metadata.getPattern(),
                                                                               definition.isUseRequestData());
            final String path = Strings.isBlank(mapping.getCapturePath())
                                ? definition.getServicePath()
                                : mapping.getCapturePath();
            logger.info("Registering route | Event Binding:\t{} {} --- {} {} {}", mapping.getMethod(), path,
                        metadata.getPattern(), mapping.getAction(), metadata.getAddress());
            HttpServer.restrictJsonRoute(
                router.route(mapping.getMethod(), path).order(definition.getOrder()).handler(restHandler));
        }
    }

}
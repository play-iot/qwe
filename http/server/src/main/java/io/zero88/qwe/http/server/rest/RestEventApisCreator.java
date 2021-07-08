package io.zero88.qwe.http.server.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;
import io.zero88.qwe.micro.httpevent.EventMethodDefinition;
import io.zero88.qwe.micro.httpevent.EventMethodMapping;
import io.zero88.qwe.micro.httpevent.RestEventApiMetadata;

import lombok.NonNull;

/**
 * @see RouterCreator
 */
public class RestEventApisCreator<T extends RouterConfig> implements ApisCreator<T> {

    private final Set<Class<? extends RestEventApi>> apis = new HashSet<>();

    @Override
    public void doLogWhenRegister(T config) {
        logger().info(config.decor("Register route [{}][{}]"), "Event APIs", config.getPath());
    }

    public RestEventApisCreator<T> register(@NonNull Class<? extends RestEventApi> restApi) {
        apis.add(restApi);
        return this;
    }

    @SafeVarargs
    public final RestEventApisCreator<T> register(Class<? extends RestEventApi>... restApi) {
        return this.register(Arrays.asList(restApi));
    }

    public RestEventApisCreator<T> register(@NonNull Collection<Class<? extends RestEventApi>> restApis) {
        restApis.stream().filter(Objects::nonNull).forEach(apis::add);
        return this;
    }

    Set<Class<? extends RestEventApi>> validate() {
        if (apis.isEmpty()) {
            throw new InitializerError("No REST API given, register at least one.");
        }
        return apis;
    }

    protected void createRouter(Router router, RestEventApi restApi, @NonNull T config,
                                @NonNull SharedDataLocalProxy sharedData) {
        restApi.registerSharedData(sharedData)
               .initRouter()
               .getRestMetadata()
               .forEach(metadata -> createRouter(router, metadata, restApi, sharedData));
    }

    protected void createRouter(Router router, RestEventApiMetadata metadata, RestEventApi api,
                                @NonNull SharedDataLocalProxy sharedData) {
        final EventMethodDefinition definition = metadata.getDefinition();
        final EventBusClient client = EventBusClient.create(sharedData);
        for (EventMethodMapping mapping : definition.getMapping()) {
            RestEventApiDispatcher restHandler = RestEventApiDispatcher.create(api.dispatcher(), client,
                                                                               metadata.getAddress(),
                                                                               mapping.getAction(),
                                                                               metadata.getPattern(),
                                                                               definition.isUseRequestData());
            String path = Strings.isBlank(mapping.getCapturePath())
                          ? definition.getServicePath()
                          : mapping.getCapturePath();
            logger().info(decor("Bind Path [{}::{}] to [{}::{}]"), Strings.padLeft(mapping.getMethod().name(), 6), path,
                          metadata.getAddress(), mapping.getAction());
            HttpServerPlugin.restrictJsonRoute(
                router.route(mapping.getMethod(), path).order(definition.getOrder()).handler(restHandler));
        }
    }

    @Override
    public @NonNull Router router(@NonNull T config, @NonNull SharedDataLocalProxy sharedData) {
        Router router = Router.router(sharedData.getVertx());
        validate().stream()
                  .map(ReflectionClass::createObject)
                  .filter(Objects::nonNull)
                  .forEach(api -> createRouter(router, api, config, sharedData));
        return router;
    }

}

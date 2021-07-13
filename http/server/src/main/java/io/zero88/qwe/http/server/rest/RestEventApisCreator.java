package io.zero88.qwe.http.server.rest;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;
import io.zero88.qwe.micro.httpevent.EventMethodDefinition;
import io.zero88.qwe.micro.httpevent.EventMethodMapping;
import io.zero88.qwe.micro.httpevent.RestEventApiMetadata;

import lombok.NonNull;

/**
 * @see RouterCreator
 */
public class RestEventApisCreator<T extends RouterConfig> extends ApisCreator<RestEventApi, T> {

    @Override
    public @NonNull Router subRouter(@NonNull T config, @NonNull SharedDataLocalProxy sharedData) {
        Router router = Router.router(sharedData.getVertx());
        getApis().stream()
                 .map(ReflectionClass::createObject)
                 .filter(Objects::nonNull)
                 .forEach(api -> createRouter(router, api, config, sharedData));
        router.route(BasePaths.addWildcards(config.getPath()));
        return router;
    }

    @Override
    protected String subFunction() {
        return "EventApi";
    }

    protected void createRouter(Router router, RestEventApi restApi, T config, SharedDataLocalProxy sharedData) {
        restApi.registerSharedData(sharedData)
               .initRouter()
               .getRestMetadata()
               .forEach(metadata -> createRouter(router, config, metadata, restApi, sharedData));
    }

    protected void createRouter(Router router, T config, RestEventApiMetadata metadata, RestEventApi api,
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

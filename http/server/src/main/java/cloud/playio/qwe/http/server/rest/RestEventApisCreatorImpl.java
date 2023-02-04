package cloud.playio.qwe.http.server.rest;

import java.nio.file.Path;
import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.ext.web.Router;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.DeliveryEvent;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.http.EventMethodMapping;
import cloud.playio.qwe.http.server.RoutePath;
import cloud.playio.qwe.http.server.RouterConfig;
import cloud.playio.qwe.http.server.handler.HttpEBDispatcher;
import cloud.playio.qwe.http.server.rest.api.IRestEventApi;
import cloud.playio.qwe.http.server.rest.handler.RestEventApiDispatcher;

import lombok.NonNull;

public abstract class RestEventApisCreatorImpl<X extends IRestEventApi<C>, C extends RouterConfig>
    extends ApisCreator<X, C> {

    @Override
    public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                                     @NonNull C config) {
        Router router = Router.router(sharedData.getVertx());
        getApis().stream()
                 .map(ReflectionClass::createObject)
                 .filter(Objects::nonNull)
                 .map(api -> api.setup(config, sharedData))
                 .forEach(api -> createRouter(router, api, config, sharedData));
        return router;
    }

    protected void createRouter(Router router, IRestEventApi<C> api, C config, SharedDataLocalProxy sharedData) {
        for (EventMethodDefinition definition : api.definitions()) {
            for (EventMethodMapping mapping : definition.getMapping()) {
                String fullPath = Urls.combinePath(config.getPath(), mapping.getCapturePath());
                logger().info(decor("Bind Path [{}::{}] to [{}::{}]"), Strings.padLeft(mapping.method(), 6), fullPath,
                              Strings.padLeft(mapping.getAction().action(), 8), api.address());
                if (!mapping.isUseRequestData()) {
                    logger().warn(decor("Path [{}] will omit data in 'HTTP Request Query' and 'HTTP Request Header'"),
                                  fullPath);
                }
                HttpEBDispatcher dispatcher = HttpEBDispatcher.create(sharedData.sharedKey(),
                                                                      new DeliveryEvent().address(api.address())
                                                                                         .pattern(api.pattern())
                                                                                         .action(mapping.getAction())
                                                                                         .useRequestData(mapping.isUseRequestData()));
                this.createRoute(router, config, RoutePath.create(mapping, api.contentTypes()), false)
                    .order(definition.getOrder())
                    .handler(RestEventApiDispatcher.create(api.dispatcher()).setup(dispatcher, mapping.getAuth()));
            }
        }
    }

}

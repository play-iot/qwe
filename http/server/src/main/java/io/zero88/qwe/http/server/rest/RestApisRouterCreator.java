package io.zero88.qwe.http.server.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import io.github.zero88.exceptions.InvalidUrlException;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.config.ApiDynamicRouteConfig;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.zero88.qwe.http.server.rest.api.RestApi;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.micro.DiscoveryContext;
import io.github.zero88.utils.Reflections;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @see RouterCreator
 */
//TODO Refactor it as RouterCreator
@RequiredArgsConstructor
public final class RestApisRouterCreator implements ApisCreator {

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final Router mainRouter;

    @NonNull
    private final Set<Class<? extends RestApi>> restApiClass = new HashSet<>();
    @NonNull
    private final Set<Class<? extends RestEventApi>> restEventApiClass = new HashSet<>();
    private String rootApi = BasePaths.ROOT_API_PATH;
    private SharedDataLocalProxy proxy;
    private ApiDynamicRouteConfig dynamicRouteConfig;

    public RestApisRouterCreator registerApi(Collection<Class<? extends RestApi>> apiClass) {
        restApiClass.addAll(apiClass);
        return this;
    }

    public RestApisRouterCreator registerEventBusApi(Collection<Class<? extends RestEventApi>> eventBusApiClasses) {
        restEventApiClass.addAll(eventBusApiClasses);
        return this;
    }

    public RestApisRouterCreator rootApi(String rootApi) {
        if (Strings.isNotBlank(rootApi)) {
            String root = Urls.combinePath(rootApi);
            if (!Urls.validatePath(root)) {
                throw new InvalidUrlException("Root API is not valid");
            }
            this.rootApi = root;
        }
        return this;
    }

    public RestApisRouterCreator addSharedDataProxy(@NonNull SharedDataLocalProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public RestApisRouterCreator dynamicRouteConfig(ApiDynamicRouteConfig dynamicRouteConfig) {
        this.dynamicRouteConfig = dynamicRouteConfig;
        return this;
    }

    public Router build() {
        if (restApiClass.isEmpty() && restEventApiClass.isEmpty() && !dynamicRouteConfig.isEnabled()) {
            throw new InitializerError("No REST API given, register at least one.");
        }
        log().info(decor("Registering sub routers in root API: '{}'..."), rootApi);
        this.addSubRouter(this::initRestApiRouter)
            .addSubRouter(this::initEventBusApiRouter)
            .addSubRouter(this::initDynamicRouter);
        HttpServerPlugin.restrictJsonRoute(
            mainRouter.route(BasePaths.addWildcards(rootApi)).handler(new EventMessageResponseHandler()));
        return mainRouter;
    }

    private RestApisRouterCreator addSubRouter(Supplier<Router> supplier) {
        final Router subRouter = supplier.get();
        if (Objects.nonNull(subRouter)) {
            mainRouter.mountSubRouter(rootApi, subRouter);
        }
        return this;
    }

    private Router initRestApiRouter() {
        if (restApiClass.isEmpty()) {
            return null;
        }
        Object[] classes = restApiClass.toArray(new Class[] {});
        log().info(decor("Registering sub router REST API..."));
        //TODO register RestAPI
        return null;
    }

    private Router initEventBusApiRouter() {
        if (restEventApiClass.isEmpty()) {
            return null;
        }
        log().info(decor("Registering sub router REST Event API..."));
        return new RestEventApisCreator(vertx).addSharedDataProxy(proxy).register(restEventApiClass).build();
    }

    private Router initDynamicRouter() {
        if (!dynamicRouteConfig.isEnabled()) {
            return null;
        }
        try {
            Class.forName(DiscoveryContext.class.getName(), false, Reflections.contextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new InitializerError("To enabled dynamic route, you have to put on qwe-core-micro.jar in classpath",
                                       e);
        }
        String path = BasePaths.addWildcards(dynamicRouteConfig.getPath());
        log().info(decor("Registering sub router REST Dynamic API '{}' in disable mode..."), path);
        Router dynamicRouter = Router.router(vertx);
        dynamicRouter.route(path).disable();
        return dynamicRouter;
    }

}

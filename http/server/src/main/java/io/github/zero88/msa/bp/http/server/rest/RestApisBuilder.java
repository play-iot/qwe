package io.github.zero88.msa.bp.http.server.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.zero88.exceptions.InvalidUrlException;
import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.http.server.ApiConstants;
import io.github.zero88.msa.bp.http.server.HttpConfig.RestConfig.DynamicRouteConfig;
import io.github.zero88.msa.bp.http.server.HttpServer;
import io.github.zero88.msa.bp.http.server.handler.ApiExceptionHandler;
import io.github.zero88.msa.bp.http.server.handler.ResponseDataWriter;
import io.github.zero88.msa.bp.http.server.handler.RestEventResponseHandler;
import io.github.zero88.msa.bp.micro.MicroContext;
import io.github.zero88.utils.Reflections;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

import com.zandero.rest.RestBuilder;
import com.zandero.rest.RestRouter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RestApisBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RestApisBuilder.class);

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final Router mainRouter;

    @NonNull
    private final Set<Class<? extends RestApi>> restApiClass = new HashSet<>();
    @NonNull
    private final Set<Class<? extends RestEventApi>> restEventApiClass = new HashSet<>();
    private String rootApi = ApiConstants.ROOT_API_PATH;
    private Function<String, Object> sharedDataFunc;
    private DynamicRouteConfig dynamicRouteConfig;

    public RestApisBuilder registerApi(Collection<Class<? extends RestApi>> apiClass) {
        restApiClass.addAll(apiClass);
        return this;
    }

    public RestApisBuilder registerEventBusApi(Collection<Class<? extends RestEventApi>> eventBusApiClasses) {
        restEventApiClass.addAll(eventBusApiClasses);
        return this;
    }

    public RestApisBuilder rootApi(String rootApi) {
        if (Strings.isNotBlank(rootApi)) {
            String root = Urls.combinePath(rootApi);
            if (!Urls.validatePath(root)) {
                throw new InvalidUrlException("Root API is not valid");
            }
            this.rootApi = root;
        }
        return this;
    }

    public RestApisBuilder addSharedDataFunc(@NonNull Function<String, Object> func) {
        this.sharedDataFunc = func;
        return this;
    }

    public RestApisBuilder dynamicRouteConfig(DynamicRouteConfig dynamicRouteConfig) {
        this.dynamicRouteConfig = dynamicRouteConfig;
        return this;
    }

    public Router build() {
        if (restApiClass.isEmpty() && restEventApiClass.isEmpty() && !dynamicRouteConfig.isEnabled()) {
            throw new InitializerError("No REST API given, register at least one.");
        }
        logger.info("Registering sub routers in root API: '{}'...", rootApi);
        this.addSubRouter(this::initRestApiRouter)
            .addSubRouter(this::initEventBusApiRouter)
            .addSubRouter(this::initDynamicRouter);
        HttpServer.restrictJsonRoute(mainRouter.route(Urls.combinePath(rootApi, ApiConstants.WILDCARDS_ANY_PATH))
                                               .handler(new RestEventResponseHandler()));
        return mainRouter;
    }

    private RestApisBuilder addSubRouter(Supplier<Router> supplier) {
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
        logger.info("Registering sub router REST API...");
        RestRouter.getExceptionHandlers().clear();
        return new RestBuilder(vertx).errorHandler(ApiExceptionHandler.class)
                                     //                                     .writer(MediaType.APPLICATION_JSON_TYPE,
                                     //                                     ApiJsonWriter.class)
                                     .writer(ResponseData.class, ResponseDataWriter.class).register(classes).build();
    }

    private Router initEventBusApiRouter() {
        if (restEventApiClass.isEmpty()) {
            return null;
        }
        logger.info("Registering sub router REST Event API...");
        return new RestEventApisBuilder(vertx).addSharedDataFunc(sharedDataFunc).register(restEventApiClass).build();
    }

    private Router initDynamicRouter() {
        if (!dynamicRouteConfig.isEnabled()) {
            return null;
        }
        try {
            Class.forName(MicroContext.class.getName(), false, Reflections.contextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new InitializerError("To enabled dynamic route, you have to put on nube-core-micro.jar in classpath",
                                       e);
        }
        String path = Urls.combinePath(dynamicRouteConfig.getPath(), ApiConstants.WILDCARDS_ANY_PATH);
        logger.info("Registering sub router REST Dynamic API '{}' in disable mode...", path);
        Router dynamicRouter = Router.router(vertx);
        dynamicRouter.route(path).disable();
        return dynamicRouter;
    }

}

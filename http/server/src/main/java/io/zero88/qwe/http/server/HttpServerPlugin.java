package io.zero88.qwe.http.server;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.zero88.qwe.PluginContext;
import io.zero88.qwe.PluginVerticle;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.config.ApiGatewayConfig;
import io.zero88.qwe.http.server.config.CorsOptions;
import io.zero88.qwe.http.server.download.DownloadRouterCreator;
import io.zero88.qwe.http.server.gateway.GatewayIndexApi;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.zero88.qwe.http.server.handler.FailureContextHandler;
import io.zero88.qwe.http.server.handler.NotFoundContextHandler;
import io.zero88.qwe.http.server.rest.RestApisRouterCreator;
import io.zero88.qwe.http.server.rest.RestEventApisCreator;
import io.zero88.qwe.http.server.rest.api.RestEventApi;
import io.zero88.qwe.http.server.upload.UploadRouterCreator;
import io.zero88.qwe.http.server.web.StaticWebRouterCreator;
import io.zero88.qwe.http.server.ws.WebSocketRouterCreator;

import lombok.NonNull;

public final class HttpServerPlugin extends PluginVerticle<HttpServerConfig, HttpServerPluginContext> {

    public final static String SERVER_INFO_DATA_KEY = "SERVER_INFO";
    public final static String SERVER_GATEWAY_ADDRESS_DATA_KEY = "SERVER_GATEWAY_ADDRESS";
    private final HttpServerRouter httpRouter;
    private HttpServer httpServer;

    HttpServerPlugin(SharedDataLocalProxy sharedData, @NonNull HttpServerRouter router) {
        super(sharedData);
        this.httpRouter = router;
    }

    @Override
    public String pluginName() {
        return "http-server";
    }

    @Override
    public Class<HttpServerConfig> configClass() { return HttpServerConfig.class; }

    @Override
    public String configFile() { return "httpServer.json"; }

    @Override
    public void onStart() {
        super.onStart();
        if (!this.pluginConfig.getApiConfig().isEnabled()) {
            this.pluginConfig.getApiConfig().getDynamicConfig().setEnabled(false);
        }
    }

    @Override
    public Future<Void> onAsyncStart() {
        logger().info("Starting HTTP Server...");
        return vertx.createHttpServer(new HttpServerOptions(pluginConfig.getOptions()).setHost(pluginConfig.getHost())
                                                                                      .setPort(pluginConfig.getPort()))
                    .requestHandler(initRouter())
                    .listen()
                    .onSuccess(server -> {
                        httpServer = server;
                        logger().info("HTTP Server started [{}:{}]", pluginConfig.getHost(), httpServer.actualPort());
                        sharedData().addData(SERVER_INFO_DATA_KEY, createServerInfo(httpServer.actualPort()));
                    })
                    .recover(t -> Future.failedFuture(QWEExceptionConverter.from(t)))
                    .mapEmpty();
    }

    @Override
    public Future<Void> onAsyncStop() {
        return Optional.ofNullable(httpServer).map(s -> httpServer.close()).orElseGet(Future::succeededFuture);
    }

    @Override
    public HttpServerPluginContext enrichPostContext(@NonNull PluginContext postContext) {
        return new HttpServerPluginContext(postContext, sharedData().getData(SERVER_INFO_DATA_KEY));
    }

    private ServerInfo createServerInfo(int port) {
        return ServerInfo.siBuilder()
                         .host(pluginConfig.getHost())
                         .port(port)
                         .publicHost(pluginConfig.publicServerUrl())
                         .apiPath(pluginConfig.getApiConfig().path())
                         .wsPath(pluginConfig.getWebSocketConfig().path())
                         .gatewayPath(pluginConfig.getApiGatewayConfig().path())
                         .servicePath(pluginConfig.getApiConfig().getDynamicConfig().path())
                         .downloadPath(pluginConfig.getFileDownloadConfig().path())
                         .uploadPath(pluginConfig.getFileUploadConfig().path())
                         .webPath(pluginConfig.getStaticWebConfig().path())
                         .router((Router) httpServer.requestHandler())
                         .build();
    }

    private Router initRouter() {
        try {
            Router root = Router.router(vertx);
            CorsOptions corsOptions = pluginConfig.getCorsOptions();
            CorsHandler corsHandler = CorsHandler.create(corsOptions.getAllowedOriginPattern())
                                                 .allowedMethods(corsOptions.allowedMethods())
                                                 .allowedHeaders(corsOptions.getAllowedHeaders())
                                                 .allowCredentials(corsOptions.isAllowCredentials())
                                                 .exposedHeaders(corsOptions.getExposedHeaders())
                                                 .maxAgeSeconds(corsOptions.getMaxAgeSeconds());
            root.route()
                .handler(corsHandler)
                .handler(ResponseContentTypeHandler.create())
                .handler(ResponseTimeHandler.create())
                .failureHandler(ResponseTimeHandler.create())
                .failureHandler(new FailureContextHandler());
            root.routeWithRegex("(?!" + pluginConfig.getFileUploadConfig().getPath() + ").+")
                .handler(BodyHandler.create(false).setBodyLimit(pluginConfig.getMaxBodySizeMB() * HttpServerConfig.MB));
            new UploadRouterCreator(pluginContext().dataDir()).mount(root, pluginConfig.getFileUploadConfig(),
                                                                     sharedData());
            new DownloadRouterCreator(pluginContext().dataDir()).mount(root, pluginConfig.getFileDownloadConfig(),
                                                                       sharedData());
            initHttp2Router(root);
            new WebSocketRouterCreator(httpRouter.getWebSocketEvents()).mount(root, pluginConfig.getWebSocketConfig(),
                                                                              sharedData());
            initRestRouter(root, pluginConfig.getApiConfig());
            initGatewayRouter(root, pluginConfig.getApiGatewayConfig());
            new StaticWebRouterCreator(pluginContext().dataDir()).mount(root, pluginConfig.getStaticWebConfig(),
                                                                        sharedData());
            root.route().last().handler(new NotFoundContextHandler());
            return root;
        } catch (QWEException e) {
            throw new InitializerError("Error when initializing HTTP Server route", e);
        }
    }

    private Router initRestRouter(Router mainRouter, ApiConfig restConfig) {
        if (!restConfig.isEnabled()) {
            return mainRouter;
        }
        return new RestApisRouterCreator(vertx, mainRouter).rootApi(restConfig.getPath())
                                                           .registerApi(httpRouter.getRestApiClasses())
                                                           .registerEventBusApi(httpRouter.getRestEventApiClasses())
                                                           .dynamicRouteConfig(restConfig.getDynamicConfig())
                                                           .addSharedData(sharedData())
                                                           .build();
    }

    private void initGatewayRouter(Router mainRouter, ApiGatewayConfig gatewayConfig) {
        if (!gatewayConfig.isEnabled()) {
            return;
        }
        this.sharedData()
            .addData(SERVER_GATEWAY_ADDRESS_DATA_KEY,
                     Strings.requireNotBlank(gatewayConfig.getAddress(), "Gateway address cannot be blank"));
        final Set<Class<? extends RestEventApi>> gatewayApis = Stream.concat(httpRouter.getGatewayApiClasses().stream(),
                                                                             Stream.of(GatewayIndexApi.class))
                                                                     .collect(Collectors.toSet());
        logger().info("GATEWAY::Registering sub routers in Gateway API: '{}'...", gatewayConfig.getPath());
        final Router gatewayRouter = new RestEventApisCreator(vertx).register(gatewayApis)
                                                                    .addSharedData(this.sharedData())
                                                                    .build();
        mainRouter.mountSubRouter(gatewayConfig.getPath(), gatewayRouter);
        restrictJsonRoute(mainRouter.route(BasePaths.addWildcards(gatewayConfig.getPath()))
                                    .handler(new EventMessageResponseHandler()));
    }

    private Router initHttp2Router(Router router) { return router; }

    /**
     * Decorator route with produce and consume
     * <p>
     * TODO: Need to check again Route#consumes(String)
     *
     * @param route route
     * @see Route#produces(String)
     * @see Route#consumes(String)
     */
    public static void restrictJsonRoute(Route route) {
        route.produces(HttpUtils.JSON_CONTENT_TYPE).produces(HttpUtils.JSON_UTF8_CONTENT_TYPE);
    }

}

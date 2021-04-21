package io.zero88.qwe.http.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zero88.qwe.component.ComponentContext;
import io.zero88.qwe.component.ComponentVerticle;
import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpConfig.ApiGatewayConfig;
import io.zero88.qwe.http.server.HttpConfig.CorsOptions;
import io.zero88.qwe.http.server.HttpConfig.FileStorageConfig;
import io.zero88.qwe.http.server.HttpConfig.FileStorageConfig.DownloadConfig;
import io.zero88.qwe.http.server.HttpConfig.FileStorageConfig.UploadConfig;
import io.zero88.qwe.http.server.HttpConfig.RestConfig;
import io.zero88.qwe.http.server.HttpConfig.RestConfig.DynamicRouteConfig;
import io.zero88.qwe.http.server.HttpConfig.StaticWebConfig;
import io.zero88.qwe.http.server.HttpConfig.WebSocketConfig;
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
import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Strings;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

import lombok.NonNull;

public final class HttpServer extends ComponentVerticle<HttpConfig, HttpServerContext> {

    public final static String SERVER_INFO_DATA_KEY = "SERVER_INFO";
    public final static String SERVER_GATEWAY_ADDRESS_DATA_KEY = "SERVER_GATEWAY_ADDRESS";
    private static final long MB = 1024L * 1024L;
    @NonNull
    private final HttpServerRouter httpRouter;
    private io.vertx.core.http.HttpServer httpServer;

    HttpServer(SharedDataLocalProxy sharedData, HttpServerRouter router) {
        super(sharedData);
        this.httpRouter = router;
    }

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

    @Override
    public void start(Promise<Void> promise) {
        logger.info("Starting HTTP Server...");
        super.start();
        final HttpServerOptions options = new HttpServerOptions(config.getOptions()).setHost(config.getHost())
                                                                                    .setPort(config.getPort());
        final Router router = initRouter();
        this.httpServer = vertx.createHttpServer(options).requestHandler(router).listen(event -> {
            if (event.succeeded()) {
                int port = event.result().actualPort();
                logger.info("Web Server started at {}", port);
                this.sharedData().addData(SERVER_INFO_DATA_KEY, createServerInfo(router, port));
                promise.complete();
                return;
            }
            promise.fail(CarlExceptionConverter.from(event.cause()));
        });
    }

    @Override
    public void stop() {
        if (Objects.nonNull(this.httpServer)) {
            this.httpServer.close();
        }
    }

    @Override
    public HttpServerContext onSuccess(@NonNull ComponentContext context) {
        return new HttpServerContext(context, sharedData().getData(SERVER_INFO_DATA_KEY));
    }

    private ServerInfo createServerInfo(Router handler, int port) {
        final RestConfig restCfg = config.getRestConfig();
        final DynamicRouteConfig dynamicCfg = restCfg.getDynamicConfig();
        final WebSocketConfig wsCfg = config.getWebSocketConfig();
        final FileStorageConfig storageCfg = config.getFileStorageConfig();
        final DownloadConfig downCfg = storageCfg.getDownloadConfig();
        final UploadConfig uploadCfg = storageCfg.getUploadConfig();
        final StaticWebConfig staticWebConfig = config.getStaticWebConfig();
        final ApiGatewayConfig gatewayConfig = config.getApiGatewayConfig();
        return ServerInfo.siBuilder()
                         .host(config.getHost())
                         .port(port)
                         .publicHost(config.publicServerUrl())
                         .apiPath(restCfg.isEnabled() ? restCfg.getRootApi() : null)
                         .wsPath(wsCfg.isEnabled() ? wsCfg.getRootWs() : null)
                         .servicePath(restCfg.isEnabled() && dynamicCfg.isEnabled() ? dynamicCfg.getPath() : null)
                         .downloadPath(storageCfg.isEnabled() && downCfg.isEnabled() ? downCfg.getPath() : null)
                         .uploadPath(storageCfg.isEnabled() && uploadCfg.isEnabled() ? uploadCfg.getPath() : null)
                         .webPath(staticWebConfig.isEnabled() ? staticWebConfig.getWebPath() : null)
                         .gatewayPath(gatewayConfig.isEnabled() ? gatewayConfig.getPath() : null)
                         .router(handler)
                         .build();
    }

    @Override
    public Class<HttpConfig> configClass() { return HttpConfig.class; }

    @Override
    public String configFile() { return "httpServer.json"; }

    private Router initRouter() {
        try {
            Router root = Router.router(vertx);
            CorsOptions corsOptions = config.getCorsOptions();
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
            final FileStorageConfig storageCfg = config.getFileStorageConfig();
            final String pathNoUpload = "(?!" + storageCfg.getUploadConfig().getPath() + ").+";
            initFileStorageRouter(root, storageCfg, config.publicServerUrl());
            root.routeWithRegex(pathNoUpload)
                .handler(BodyHandler.create(false).setBodyLimit(config.getMaxBodySizeMB() * MB));
            initHttp2Router(root);
            new WebSocketRouterCreator(httpRouter.getWebSocketEvents()).mount(root, config.getWebSocketConfig(),
                                                                              sharedData());
            initRestRouter(root, config.getRestConfig());
            initGatewayRouter(root, config.getApiGatewayConfig());
            new StaticWebRouterCreator().mount(root, config.getStaticWebConfig(), sharedData());
            root.route().last().handler(new NotFoundContextHandler());
            return root;
        } catch (CarlException e) {
            throw new InitializerError("Error when initializing http server route", e);
        }
    }

    private Router initRestRouter(Router mainRouter, RestConfig restConfig) {
        if (!restConfig.isEnabled()) {
            return mainRouter;
        }
        return new RestApisRouterCreator(vertx, mainRouter).rootApi(restConfig.getRootApi())
                                                           .registerApi(httpRouter.getRestApiClasses())
                                                           .registerEventBusApi(httpRouter.getRestEventApiClasses())
                                                           .dynamicRouteConfig(restConfig.getDynamicConfig())
                                                           .addSharedDataProxy(this.sharedData())
                                                           .build();
    }

    private Router initFileStorageRouter(Router router, FileStorageConfig storageCfg, String publicUrl) {
        if (!storageCfg.isEnabled()) {
            return router;
        }
        final Path storageDir = Paths.get(FileUtils.createFolder(getContext().dataDir(), storageCfg.getDir()));
        new UploadRouterCreator(storageDir, publicUrl).mount(router, storageCfg.getUploadConfig(), sharedData());
        new DownloadRouterCreator(storageDir).mount(router, storageCfg.getDownloadConfig(), sharedData());
        return router;
    }

    private void initGatewayRouter(Router mainRouter, ApiGatewayConfig apiGatewayConfig) {
        if (!apiGatewayConfig.isEnabled()) {
            return;
        }
        this.sharedData()
            .addData(SERVER_GATEWAY_ADDRESS_DATA_KEY,
                     Strings.requireNotBlank(apiGatewayConfig.getAddress(), "Gateway address cannot be blank"));
        final Set<Class<? extends RestEventApi>> gatewayApis = Stream.concat(httpRouter.getGatewayApiClasses().stream(),
                                                                             Stream.of(GatewayIndexApi.class))
                                                                     .collect(Collectors.toSet());
        logger.info("GATEWAY::Registering sub routers in Gateway API: '{}'...", apiGatewayConfig.getPath());
        final Router gatewayRouter = new RestEventApisCreator(vertx).register(gatewayApis)
                                                                    .addSharedDataProxy(this.sharedData())
                                                                    .build();
        mainRouter.mountSubRouter(apiGatewayConfig.getPath(), gatewayRouter);
        restrictJsonRoute(mainRouter.route(BasePaths.addWildcards(apiGatewayConfig.getPath()))
                                    .handler(new EventMessageResponseHandler()));
    }

    private Router initHttp2Router(Router router) { return router; }

}

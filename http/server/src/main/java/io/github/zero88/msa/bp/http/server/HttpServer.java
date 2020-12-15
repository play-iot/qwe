package io.github.zero88.msa.bp.http.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.msa.bp.BlueprintConfig;
import io.github.zero88.msa.bp.component.SharedDataDelegate;
import io.github.zero88.msa.bp.component.UnitVerticle;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.exceptions.converter.BlueprintExceptionConverter;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.github.zero88.msa.bp.http.server.HttpConfig.ApiGatewayConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.CorsOptions;
import io.github.zero88.msa.bp.http.server.HttpConfig.FileStorageConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.FileStorageConfig.DownloadConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.FileStorageConfig.UploadConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.RestConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.RestConfig.DynamicRouteConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.StaticWebConfig;
import io.github.zero88.msa.bp.http.server.HttpConfig.WebSocketConfig;
import io.github.zero88.msa.bp.http.server.gateway.GatewayIndexApi;
import io.github.zero88.msa.bp.http.server.handler.DownloadFileHandler;
import io.github.zero88.msa.bp.http.server.handler.FailureContextHandler;
import io.github.zero88.msa.bp.http.server.handler.NotFoundContextHandler;
import io.github.zero88.msa.bp.http.server.handler.RestEventResponseHandler;
import io.github.zero88.msa.bp.http.server.handler.UploadFileHandler;
import io.github.zero88.msa.bp.http.server.handler.UploadListener;
import io.github.zero88.msa.bp.http.server.handler.WebSocketBridgeEventHandler;
import io.github.zero88.msa.bp.http.server.rest.RestApisBuilder;
import io.github.zero88.msa.bp.http.server.rest.RestEventApi;
import io.github.zero88.msa.bp.http.server.rest.RestEventApisBuilder;
import io.github.zero88.msa.bp.http.server.ws.WebSocketEventBuilder;
import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.StaticHandler;

import lombok.NonNull;

public final class HttpServer extends UnitVerticle<HttpConfig, HttpServerContext> {

    public final static String SERVER_INFO_DATA_KEY = "SERVER_INFO";
    public final static String SERVER_GATEWAY_ADDRESS_DATA_KEY = "SERVER_GATEWAY_ADDRESS";
    private static final long MB = 1024L * 1024L;
    @NonNull
    private final HttpServerRouter httpRouter;
    private io.vertx.core.http.HttpServer httpServer;
    private String dataDir;

    HttpServer(HttpServerRouter httpRouter) {
        super(new HttpServerContext());
        this.httpRouter = httpRouter;
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
    public void start(Future<Void> future) {
        logger.info("Starting HTTP Server...");
        super.start();
        this.dataDir = this.getSharedData(SharedDataDelegate.SHARED_DATADIR, BlueprintConfig.DEFAULT_DATADIR.toString());
        HttpServerOptions options = new HttpServerOptions(config.getOptions()).setHost(config.getHost())
                                                                              .setPort(config.getPort());
        final Router handler = initRouter();
        this.httpServer = vertx.createHttpServer(options).requestHandler(handler).listen(event -> {
            if (event.succeeded()) {
                int port = event.result().actualPort();
                logger.info("Web Server started at {}", port);
                this.getContext().setup(addSharedData(SERVER_INFO_DATA_KEY, createServerInfo(handler, port)));
                future.complete();
                return;
            }
            future.fail(BlueprintExceptionConverter.from(event.cause()));
        });
    }

    @Override
    public void stop() {
        if (Objects.nonNull(this.httpServer)) {
            this.httpServer.close();
        }
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
            Router mainRouter = Router.router(vertx);
            CorsOptions corsOptions = config.getCorsOptions();
            CorsHandler corsHandler = CorsHandler.create(corsOptions.getAllowedOriginPattern())
                                                 .allowedMethods(corsOptions.getAllowedMethods())
                                                 .allowedHeaders(corsOptions.getAllowedHeaders())
                                                 .allowCredentials(corsOptions.isAllowCredentials())
                                                 .exposedHeaders(corsOptions.getExposedHeaders())
                                                 .maxAgeSeconds(corsOptions.getMaxAgeSeconds());
            mainRouter.route()
                      .handler(corsHandler)
                      .handler(ResponseContentTypeHandler.create())
                      .handler(ResponseTimeHandler.create())
                      .failureHandler(ResponseTimeHandler.create())
                      .failureHandler(new FailureContextHandler());
            String pathNoUpload = "(?!" + config.getFileStorageConfig().getUploadConfig().getPath() + ").+";
            initFileStorageRouter(mainRouter, config.getFileStorageConfig(), config.publicServerUrl());
            mainRouter.routeWithRegex(pathNoUpload)
                      .handler(BodyHandler.create(false).setBodyLimit(config.getMaxBodySizeMB() * MB));
            initWebSocketRouter(mainRouter, config.getWebSocketConfig());
            initHttp2Router(mainRouter);
            initRestRouter(mainRouter, config.getRestConfig());
            initGatewayRouter(mainRouter, config.getApiGatewayConfig());
            initStaticWebRouter(mainRouter, config.getStaticWebConfig());
            mainRouter.route().last().handler(new NotFoundContextHandler());
            return mainRouter;
        } catch (BlueprintException e) {
            throw new InitializerError("Error when initializing http server route", e);
        }
    }

    private void initStaticWebRouter(Router mainRouter, StaticWebConfig webConfig) {
        if (!webConfig.isEnabled()) {
            return;
        }
        final StaticHandler staticHandler = StaticHandler.create();
        if (webConfig.isInResource()) {
            staticHandler.setWebRoot(webConfig.getWebRoot());
        } else {
            String webDir = FileUtils.createFolder(BlueprintConfig.DEFAULT_DATADIR, dataDir, webConfig.getWebRoot());
            logger.info("Static web dir {}", webDir);
            staticHandler.setEnableRangeSupport(true)
                         .setSendVaryHeader(true)
                         .setFilesReadOnly(false)
                         .setAllowRootFileSystemAccess(true)
                         .setIncludeHidden(false)
                         .setWebRoot(webDir);
        }
        mainRouter.route(Urls.combinePath(webConfig.getWebPath(), ApiConstants.WILDCARDS_ANY_PATH))
                  .handler(staticHandler);
    }

    private Router initRestRouter(Router mainRouter, RestConfig restConfig) {
        if (!restConfig.isEnabled()) {
            return mainRouter;
        }
        return new RestApisBuilder(vertx, mainRouter).rootApi(restConfig.getRootApi())
                                                     .registerApi(httpRouter.getRestApiClasses())
                                                     .registerEventBusApi(httpRouter.getRestEventApiClasses())
                                                     .dynamicRouteConfig(restConfig.getDynamicConfig())
                                                     .addSharedDataFunc(this::getSharedData)
                                                     .build();
    }

    private Router initFileStorageRouter(Router router, FileStorageConfig storageCfg, String publicUrl) {
        if (!storageCfg.isEnabled()) {
            return router;
        }
        Path storageDir = Paths.get(FileUtils.createFolder(BlueprintConfig.DEFAULT_DATADIR, dataDir, storageCfg.getDir()));
        initUploadRouter(router, storageDir, storageCfg.getUploadConfig(), publicUrl);
        initDownloadRouter(router, storageDir, storageCfg.getDownloadConfig());
        return router;
    }

    private void initGatewayRouter(Router mainRouter, ApiGatewayConfig apiGatewayConfig) {
        if (!apiGatewayConfig.isEnabled()) {
            return;
        }
        addSharedData(SERVER_GATEWAY_ADDRESS_DATA_KEY,
                      Strings.requireNotBlank(apiGatewayConfig.getAddress(), "Gateway address cannot be blank"));
        final Set<Class<? extends RestEventApi>> gatewayApis = Stream.concat(httpRouter.getGatewayApiClasses().stream(),
                                                                             Stream.of(GatewayIndexApi.class))
                                                                     .collect(Collectors.toSet());
        logger.info("Registering sub routers in Gateway API: '{}'...", apiGatewayConfig.getPath());
        final Router gatewayRouter = new RestEventApisBuilder(vertx).register(gatewayApis)
                                                                    .addSharedDataFunc(this::getSharedData)
                                                                    .build();
        final String wildcardsPath = Urls.combinePath(apiGatewayConfig.getPath(), ApiConstants.WILDCARDS_ANY_PATH);
        mainRouter.mountSubRouter(apiGatewayConfig.getPath(), gatewayRouter);
        restrictJsonRoute(mainRouter.route(wildcardsPath).handler(new RestEventResponseHandler()));
    }

    private Router initDownloadRouter(Router router, Path storageDir, DownloadConfig downloadCfg) {
        if (!downloadCfg.isEnabled()) {
            return router;
        }
        logger.info("Init Download router: '{}'...", downloadCfg.getPath());
        router.get(Urls.combinePath(downloadCfg.getPath(), ApiConstants.WILDCARDS_ANY_PATH))
              .handler(StaticHandler.create()
                                    .setEnableRangeSupport(true)
                                    .setSendVaryHeader(true)
                                    .setFilesReadOnly(false)
                                    .setAllowRootFileSystemAccess(true)
                                    .setIncludeHidden(false)
                                    .setWebRoot(storageDir.toString()))
              .handler(DownloadFileHandler.create(downloadCfg.getHandlerClass(), downloadCfg.getPath(), storageDir));
        return router;
    }

    private Router initWebSocketRouter(Router router, WebSocketConfig websocketCfg) {
        if (!websocketCfg.isEnabled()) {
            return router;
        }
        logger.info("Init Websocket router...");
        return new WebSocketEventBuilder(vertx, router, getSharedKey()).rootWs(websocketCfg.getRootWs())
                                                                       .register(httpRouter.getWebSocketEvents())
                                                                       .handler(WebSocketBridgeEventHandler.class)
                                                                       .options(websocketCfg)
                                                                       .build();
    }

    private Router initHttp2Router(Router router) { return router; }

    private Router initUploadRouter(Router router, Path storageDir, UploadConfig uploadCfg, String publicUrl) {
        if (!uploadCfg.isEnabled()) {
            return router;
        }
        logger.info("Init Upload router: '{}'...", uploadCfg.getPath());
        EventbusClient controller = SharedDataDelegate.getEventController(vertx, getSharedKey());
        EventModel listenerEvent = EventModel.builder()
                                             .address(Strings.fallback(uploadCfg.getListenerAddress(),
                                                                       getSharedKey() + ".upload"))
                                             .event(EventAction.CREATE)
                                             .pattern(EventPattern.REQUEST_RESPONSE)
                                             .local(true)
                                             .build();
        String handlerClass = uploadCfg.getHandlerClass();
        String listenerClass = uploadCfg.getListenerClass();
        controller.register(listenerEvent, UploadListener.create(vertx, listenerClass, getSharedKey(),
                                                                 new ArrayList<>(listenerEvent.getEvents())));
        router.post(uploadCfg.getPath())
              .handler(BodyHandler.create(storageDir.toString()).setBodyLimit(uploadCfg.getMaxBodySizeMB() * MB))
              .handler(UploadFileHandler.create(handlerClass, controller, listenerEvent, storageDir, publicUrl))
              .handler(new RestEventResponseHandler())
              .produces(HttpUtils.JSON_CONTENT_TYPE)
              .produces(HttpUtils.JSON_UTF8_CONTENT_TYPE);
        return router;
    }

}

package io.zero88.qwe.http.server;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.utils.HttpScheme;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Urls;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.zero88.qwe.ComponentConfig;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.QWEAppConfig;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.download.DownloadFileHandler;
import io.zero88.qwe.http.server.upload.UploadFileHandler;
import io.zero88.qwe.http.server.upload.UploadListener;
import io.zero88.qwe.http.server.ws.WebSocketBridgeEventHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class HttpConfig implements ComponentConfig {

    public static final String NAME = "__http__";
    private String host = "0.0.0.0";
    private int port = 8080;
    @Getter(value = AccessLevel.PRIVATE)
    private String publicHost = "";
    @Getter(value = AccessLevel.PRIVATE)
    private int publicPort = -1;
    private HttpScheme publicScheme = HttpScheme.HTTP;
    private int maxBodySizeMB = 2;

    @JsonProperty(value = ServerOptions.NAME)
    private ServerOptions options = (ServerOptions) new ServerOptions().setCompressionSupported(true)
                                                                       .setDecompressionSupported(true);
    @JsonProperty(value = RestConfig.NAME)
    private RestConfig restConfig = new RestConfig();
    @JsonProperty(value = WebSocketConfig.NAME)
    private WebSocketConfig webSocketConfig = new WebSocketConfig();
    @JsonProperty(value = Http2Config.NAME)
    private Http2Config http2Cfg = new Http2Config();
    @JsonProperty(value = CorsOptions.NAME)
    private CorsOptions corsOptions = new CorsOptions();
    @JsonProperty(value = ApiGatewayConfig.NAME)
    private ApiGatewayConfig apiGatewayConfig = new ApiGatewayConfig();
    @JsonProperty(value = FileStorageConfig.NAME)
    private FileStorageConfig fileStorageConfig = new FileStorageConfig();
    @JsonProperty(value = StaticWebConfig.NAME)
    private StaticWebConfig staticWebConfig = new StaticWebConfig();

    @Override
    public String key() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return QWEAppConfig.class; }

    @JsonIgnore
    public String publicServerUrl() {
        return Urls.buildURL(publicScheme, publicHost, publicPort);
    }

    @NoArgsConstructor
    public static class ServerOptions extends HttpServerOptions implements IConfig {

        public static final String NAME = "__options__";

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Override
        public JsonObject toJson() { return super.toJson(); }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class RestConfig implements IConfig, RouterConfig {

        public static final String NAME = "__rest__";

        private boolean enabled = true;
        private String rootApi = BasePaths.ROOT_API_PATH;
        @JsonProperty(value = DynamicRouteConfig.NAME)
        private DynamicRouteConfig dynamicConfig = new DynamicRouteConfig();

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Override
        public String basePath() {
            return rootApi;
        }

        @Getter
        @Setter(value = AccessLevel.PACKAGE)
        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        public static class DynamicRouteConfig implements IConfig, RouterConfig {

            public static final String NAME = "__dynamic__";

            private boolean enabled = false;
            private String path = BasePaths.DYNAMIC_API_PATH;

            @Override
            public String key() { return NAME; }

            @Override
            public Class<? extends IConfig> parent() { return RestConfig.class; }

            @Override
            public String basePath() {
                return path;
            }

        }

    }


    @Getter
    @Builder
    @Jacksonized
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    public static class WebSocketConfig implements IConfig, RouterConfig {

        public static final String NAME = "__websocket__";
        @Default
        @Setter(value = AccessLevel.PACKAGE)
        private boolean enabled = false;
        @Default
        private final String rootWs = BasePaths.ROOT_WS_PATH;
        @Default
        private final String bridgeHandlerClass = WebSocketBridgeEventHandler.class.getName();
        @Default
        @JsonProperty(value = SockJSConfig.NAME)
        private final SockJSConfig sockjsOptions = new SockJSConfig();
        @Default
        @JsonProperty(value = SocketBridgeConfig.NAME)
        private final SocketBridgeConfig bridgeOptions = new SocketBridgeConfig();

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Override
        public String basePath() {
            return rootWs;
        }

        public Class<? extends WebSocketBridgeEventHandler> bridgeHandlerClass() {
            return Optional.ofNullable(ReflectionClass.<WebSocketBridgeEventHandler>findClass(bridgeHandlerClass))
                           .orElse(WebSocketBridgeEventHandler.class);
        }

        public static class SockJSConfig extends SockJSHandlerOptions implements IConfig {

            public static final String NAME = "__sockjs__";

            @Override
            public String key() { return NAME; }

            @Override
            public Class<? extends IConfig> parent() { return WebSocketConfig.class; }

        }


        public static class SocketBridgeConfig extends SockJSBridgeOptions implements IConfig {

            public static final String NAME = "__bridge__";

            @Override
            public String key() { return NAME; }

            @Override
            public Class<? extends IConfig> parent() { return WebSocketConfig.class; }

        }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Http2Config implements IConfig {

        public static final String NAME = "__http2__";

        private boolean enabled = false;

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class CorsOptions implements IConfig {

        public static final String NAME = "__cors__";

        private String allowedOriginPattern = "*";
        private Set<String> allowedMethods = defaultAllowMethods();
        private Set<String> allowedHeaders = new HashSet<>();
        private Set<String> exposedHeaders = new HashSet<>();
        private boolean allowCredentials = false;
        private int maxAgeSeconds = 3600;

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        public Set<HttpMethod> allowedMethods() {
            return this.allowedMethods.stream().map(HttpMethod::valueOf).collect(Collectors.toSet());
        }

        static Set<String> defaultAllowMethods() {
            return HttpUtils.DEFAULT_CORS_HTTP_METHOD.stream().map(HttpMethod::name).collect(Collectors.toSet());
        }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class FileStorageConfig implements IConfig {

        public static final String NAME = "__files__";

        private boolean enabled = false;
        private String dir = "files";
        @JsonProperty(value = UploadConfig.NAME)
        private UploadConfig uploadConfig = new UploadConfig();
        @JsonProperty(value = DownloadConfig.NAME)
        private DownloadConfig downloadConfig = new DownloadConfig();

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Getter
        @Setter(value = AccessLevel.PACKAGE)
        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        public static class UploadConfig implements IConfig, RouterConfig {

            public static final String NAME = "__upload__";

            private boolean enabled = false;
            private String path = BasePaths.ROOT_UPLOAD_PATH;
            private int maxBodySizeMB = 10;
            private String handlerClass = UploadFileHandler.class.getName();
            private String listenerAddress;
            private String listenerClass = UploadListener.class.getName();

            @Override
            public String key() { return NAME; }

            @Override
            public Class<? extends IConfig> parent() { return FileStorageConfig.class; }

            @Override
            public String basePath() {
                return path;
            }

        }


        @Getter
        @Setter(value = AccessLevel.PACKAGE)
        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        public static class DownloadConfig implements IConfig, RouterConfig {

            public static final String NAME = "__download__";

            private boolean enabled = false;
            private String path = BasePaths.ROOT_DOWNLOAD_PATH;
            private String handlerClass = DownloadFileHandler.class.getName();

            @Override
            public String key() { return NAME; }

            @Override
            public Class<? extends IConfig> parent() { return FileStorageConfig.class; }

            @Override
            public String basePath() {
                return path;
            }

        }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class ApiGatewayConfig implements IConfig, RouterConfig {

        public static final String NAME = "__api_gateway__";

        private boolean enabled = false;
        private String path = BasePaths.ROOT_GATEWAY_PATH;
        private String address;

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Override
        public String basePath() {
            return path;
        }

    }


    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class StaticWebConfig implements IConfig, RouterConfig {

        public static final String NAME = "__static__";
        private boolean enabled = false;
        private boolean inResource = true;
        private String webPath = BasePaths.WEB_PATH;
        private String webRoot = "webroot";

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return HttpConfig.class; }

        @Override
        public String basePath() {
            return webPath;
        }

    }

}

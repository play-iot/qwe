package io.zero88.qwe.http.server;

import io.github.zero88.utils.HttpScheme;
import io.github.zero88.utils.Urls;
import io.vertx.core.http.HttpServerOptions;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.PluginConfig;
import io.zero88.qwe.QWEAppConfig;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.config.ApiGatewayConfig;
import io.zero88.qwe.http.server.config.CorsOptions;
import io.zero88.qwe.http.server.config.FileDownloadConfig;
import io.zero88.qwe.http.server.config.FileUploadConfig;
import io.zero88.qwe.http.server.config.Http2Config;
import io.zero88.qwe.http.server.config.StaticWebConfig;
import io.zero88.qwe.http.server.config.WebSocketConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class HttpConfig implements PluginConfig {

    public static final long MB = 1024L * 1024L;

    public static final String NAME = "__http__";
    private String host = "0.0.0.0";
    private int port = 8080;
    @Getter(value = AccessLevel.PRIVATE)
    private String publicHost = "";
    @Getter(value = AccessLevel.PRIVATE)
    private int publicPort = -1;
    private HttpScheme publicScheme = HttpScheme.HTTP;
    private int maxBodySizeMB = 2;
    private String fileDir = "http";

    @JsonProperty(value = ServerOptions.NAME)
    private ServerOptions options = (ServerOptions) new ServerOptions().setCompressionSupported(true)
                                                                       .setDecompressionSupported(true);
    @JsonProperty(value = ApiConfig.NAME)
    private ApiConfig apiConfig = (ApiConfig) new ApiConfig().setEnabled(true);
    @JsonProperty(value = ApiGatewayConfig.NAME)
    private ApiGatewayConfig apiGatewayConfig = new ApiGatewayConfig();
    @JsonProperty(value = WebSocketConfig.NAME)
    private WebSocketConfig webSocketConfig = new WebSocketConfig();
    @JsonProperty(value = Http2Config.NAME)
    private Http2Config http2Cfg = new Http2Config();
    @JsonProperty(value = CorsOptions.NAME)
    private CorsOptions corsOptions = new CorsOptions();
    @JsonProperty(value = FileDownloadConfig.NAME)
    private FileDownloadConfig fileDownloadConfig = new FileDownloadConfig();
    @JsonProperty(value = FileUploadConfig.NAME)
    private FileUploadConfig fileUploadConfig = new FileUploadConfig();
    @JsonProperty(value = StaticWebConfig.NAME)
    private StaticWebConfig staticWebConfig = new StaticWebConfig();

    @Override
    public String key() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return QWEAppConfig.class; }

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

    }

}

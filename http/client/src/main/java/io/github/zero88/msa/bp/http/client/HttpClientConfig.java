package io.github.zero88.msa.bp.http.client;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.github.zero88.msa.bp.BlueprintConfig.AppConfig;
import io.github.zero88.msa.bp.IConfig;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.client.handler.HttpClientWriter;
import io.github.zero88.msa.bp.http.client.handler.HttpErrorHandler;
import io.github.zero88.msa.bp.http.client.handler.HttpHeavyResponseHandler;
import io.github.zero88.msa.bp.http.client.handler.HttpLightResponseBodyHandler;
import io.github.zero88.msa.bp.http.client.handler.WsConnectErrorHandler;
import io.github.zero88.msa.bp.http.client.handler.WsLightResponseDispatcher;
import io.github.zero88.msa.bp.http.client.handler.WsResponseErrorHandler;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpClientOptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
public final class HttpClientConfig implements IConfig {

    public static final int CONNECT_TIMEOUT_SECOND = 45;
    public static final int HTTP_IDLE_TIMEOUT_SECOND = 15;
    public static final int WS_IDLE_TIMEOUT_SECOND = 1200;
    private String userAgent = "zbp.httpclient";
    private HostInfo hostInfo;
    private HttpClientOptions options;
    private HandlerConfig handlerConfig = new HandlerConfig();

    HttpClientConfig() {
        this(new HttpClientOptions().setIdleTimeout(HTTP_IDLE_TIMEOUT_SECOND)
                                    .setIdleTimeoutUnit(TimeUnit.SECONDS)
                                    .setConnectTimeout(CONNECT_TIMEOUT_SECOND * 1000)
                                    .setTryUseCompression(true)
                                    .setWebsocketCompressionAllowClientNoContext(true)
                                    .setWebsocketCompressionRequestServerNoContext(true)
                                    .setWebsocketCompressionLevel(6)
                                    .setTryUsePerFrameWebsocketCompression(false)
                                    .setTryUsePerMessageWebsocketCompression(true));
    }

    HttpClientConfig(@NonNull HttpClientOptions options) {
        this.options = options;
    }

    public static HttpClientConfig create(String userAgent, @NonNull HostInfo info) {
        final HttpClientConfig config = new HttpClientConfig(new HttpClientOptions());
        config.hostInfo = info;
        config.userAgent = Strings.isBlank(userAgent) ? config.userAgent : userAgent;
        return config;
    }

    @Override
    public String key() { return "__httpClient__"; }

    @Override
    public Class<? extends IConfig> parent() { return AppConfig.class; }

    public HostInfo getHostInfo() {
        if (Objects.nonNull(hostInfo)) {
            return hostInfo;
        }
        return initHostInfo();
    }

    private synchronized HostInfo initHostInfo() {
        hostInfo = HostInfo.builder()
                           .host(this.getOptions().getDefaultHost())
                           .port(this.getOptions().getDefaultPort())
                           .ssl(this.getOptions().isSsl())
                           .build();
        return hostInfo;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HandlerConfig {

        private Class<? extends HttpClientWriter> httpClientWriterClass = HttpClientWriter.class;
        private Class<? extends HttpLightResponseBodyHandler> httpLightBodyHandlerClass
            = HttpLightResponseBodyHandler.class;
        private Class<? extends HttpHeavyResponseHandler> httpHeavyBodyHandlerClass = HttpHeavyResponseHandler.class;
        private Class<? extends HttpErrorHandler> httpErrorHandlerClass = HttpErrorHandler.class;
        private Class<? extends WsConnectErrorHandler> wsConnectErrorHandlerClass = WsConnectErrorHandler.class;
        private Class<? extends WsResponseErrorHandler> wsErrorHandlerClass = WsResponseErrorHandler.class;
        private Class<? extends WsLightResponseDispatcher> wsLightResponseHandlerClass
            = WsLightResponseDispatcher.class;

        @JsonCreator
        HandlerConfig(@JsonProperty("httpClientWriterClass") String httpClientWriterClass,
                      @JsonProperty("httpLightBodyHandlerClass") String httpLightBodyHandlerClass,
                      @JsonProperty("httpHeavyBodyHandlerClass") String httpHeavyBodyHandlerClass,
                      @JsonProperty("httpErrorHandlerClass") String httpErrorHandlerClass,
                      @JsonProperty("wsConnectErrorHandlerClass") String wsConnectErrorHandlerClass,
                      @JsonProperty("wsErrorHandlerClass") String wsErrorHandlerClass,
                      @JsonProperty("wsLightResponseHandlerClass") String wsLightResponseHandlerClass) {
            this.httpClientWriterClass = Strings.isBlank(httpClientWriterClass)
                                         ? HttpClientWriter.class
                                         : ReflectionClass.findClass(httpClientWriterClass);
            this.httpLightBodyHandlerClass = Strings.isBlank(httpLightBodyHandlerClass)
                                             ? HttpLightResponseBodyHandler.class
                                             : ReflectionClass.findClass(httpLightBodyHandlerClass);
            this.httpHeavyBodyHandlerClass = Strings.isBlank(httpHeavyBodyHandlerClass)
                                             ? HttpHeavyResponseHandler.class
                                             : ReflectionClass.findClass(httpHeavyBodyHandlerClass);
            this.httpErrorHandlerClass = Strings.isBlank(httpErrorHandlerClass)
                                         ? HttpErrorHandler.class
                                         : ReflectionClass.findClass(httpErrorHandlerClass);
            this.wsConnectErrorHandlerClass = Strings.isBlank(wsConnectErrorHandlerClass)
                                              ? WsConnectErrorHandler.class
                                              : ReflectionClass.findClass(wsConnectErrorHandlerClass);
            this.wsErrorHandlerClass = Strings.isBlank(wsErrorHandlerClass)
                                       ? WsResponseErrorHandler.class
                                       : ReflectionClass.findClass(wsErrorHandlerClass);
            this.wsLightResponseHandlerClass = Strings.isBlank(wsLightResponseHandlerClass)
                                               ? WsLightResponseDispatcher.class
                                               : ReflectionClass.findClass(wsLightResponseHandlerClass);
        }

    }

}

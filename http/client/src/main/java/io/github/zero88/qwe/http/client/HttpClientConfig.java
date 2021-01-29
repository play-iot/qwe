package io.github.zero88.qwe.http.client;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.github.zero88.qwe.CarlConfig.AppConfig;
import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.http.HostInfo;
import io.github.zero88.qwe.http.client.handler.HttpErrorHandler;
import io.github.zero88.qwe.http.client.handler.HttpRequestMessageComposer;
import io.github.zero88.qwe.http.client.handler.HttpResponseBinaryHandler;
import io.github.zero88.qwe.http.client.handler.HttpResponseTextHandler;
import io.github.zero88.qwe.http.client.handler.WebSocketConnectErrorHandler;
import io.github.zero88.qwe.http.client.handler.WebSocketResponseDispatcher;
import io.github.zero88.qwe.http.client.handler.WebSocketResponseErrorHandler;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientConfig implements IConfig {

    public static final int CONNECT_TIMEOUT_SECOND = 45;
    public static final int HTTP_IDLE_TIMEOUT_SECOND = 15;
    public static final int WS_IDLE_TIMEOUT_SECOND = 1200;
    private String userAgent = "qwe.httpclient";
    private HostInfo hostInfo;
    private HttpClientOptions options;
    private HandlerConfig handlerConfig = new HandlerConfig();

    HttpClientConfig() {
        this(new HttpClientOptions().setIdleTimeout(HTTP_IDLE_TIMEOUT_SECOND)
                                    .setIdleTimeoutUnit(TimeUnit.SECONDS)
                                    .setConnectTimeout(CONNECT_TIMEOUT_SECOND * 1000)
                                    .setTryUseCompression(true)
                                    .setWebSocketCompressionLevel(6)
                                    .setWebSocketCompressionAllowClientNoContext(true)
                                    .setWebSocketCompressionRequestServerNoContext(true)
                                    .setTryUsePerFrameWebSocketCompression(false)
                                    .setTryUsePerMessageWebSocketCompression(true));
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

    @JsonCreator
    static HttpClientConfig create(@JsonProperty("userAgent") String userAgent,
                                   @JsonProperty("options") JsonObject options,
                                   @JsonProperty("handlerConfig") JsonObject handlerConfig) {
        return new HttpClientConfig(userAgent, null, new HttpClientOptions(options),
                                    JsonData.convert(handlerConfig, HandlerConfig.class));
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

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("options", options.toJson())
                               .put("handlerConfig", JsonData.tryParse(this.handlerConfig).toJson())
                               .put("userAgent", this.userAgent);
    }

    @Getter
    @FieldNameConstants
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HandlerConfig {

        private Class<? extends HttpRequestMessageComposer> reqComposerCls = HttpRequestMessageComposer.class;
        private Class<? extends HttpResponseTextHandler> respTextHandlerCls = HttpResponseTextHandler.class;
        private Class<? extends HttpResponseBinaryHandler> respBinaryHandlerCls = HttpResponseBinaryHandler.class;
        private Class<? extends HttpErrorHandler> httpErrorHandlerCls = HttpErrorHandler.class;
        private Class<? extends WebSocketConnectErrorHandler> webSocketConnectErrorHandlerCls
            = WebSocketConnectErrorHandler.class;
        private Class<? extends WebSocketResponseErrorHandler> webSocketErrorHandlerCls
            = WebSocketResponseErrorHandler.class;
        private Class<? extends WebSocketResponseDispatcher> webSocketResponseDispatcherCls
            = WebSocketResponseDispatcher.class;

        @JsonCreator
        HandlerConfig(@JsonProperty(Fields.reqComposerCls) String reqComposerCls,
                      @JsonProperty(Fields.respTextHandlerCls) String respTextHandlerCls,
                      @JsonProperty(Fields.respBinaryHandlerCls) String respBinaryHandlerCls,
                      @JsonProperty(Fields.httpErrorHandlerCls) String httpErrorHandlerCls,
                      @JsonProperty(Fields.webSocketConnectErrorHandlerCls) String webSocketConnectErrorHandlerCls,
                      @JsonProperty(Fields.webSocketErrorHandlerCls) String webSocketErrorHandlerCls,
                      @JsonProperty(Fields.webSocketResponseDispatcherCls) String webSocketResponseDispatcherCls) {
            this.reqComposerCls = Strings.isBlank(reqComposerCls)
                                  ? HttpRequestMessageComposer.class
                                  : ReflectionClass.findClass(reqComposerCls);
            this.respTextHandlerCls = Strings.isBlank(respTextHandlerCls)
                                      ? HttpResponseTextHandler.class
                                      : ReflectionClass.findClass(respTextHandlerCls);
            this.respBinaryHandlerCls = Strings.isBlank(respBinaryHandlerCls)
                                        ? HttpResponseBinaryHandler.class
                                        : ReflectionClass.findClass(respBinaryHandlerCls);
            this.httpErrorHandlerCls = Strings.isBlank(httpErrorHandlerCls)
                                       ? HttpErrorHandler.class
                                       : ReflectionClass.findClass(httpErrorHandlerCls);
            this.webSocketConnectErrorHandlerCls = Strings.isBlank(webSocketConnectErrorHandlerCls)
                                                   ? WebSocketConnectErrorHandler.class
                                                   : ReflectionClass.findClass(webSocketConnectErrorHandlerCls);
            this.webSocketErrorHandlerCls = Strings.isBlank(webSocketErrorHandlerCls)
                                            ? WebSocketResponseErrorHandler.class
                                            : ReflectionClass.findClass(webSocketErrorHandlerCls);
            this.webSocketResponseDispatcherCls = Strings.isBlank(webSocketResponseDispatcherCls)
                                                  ? WebSocketResponseDispatcher.class
                                                  : ReflectionClass.findClass(webSocketResponseDispatcherCls);
        }

    }

}

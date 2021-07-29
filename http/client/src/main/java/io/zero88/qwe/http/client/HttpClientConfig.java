package io.zero88.qwe.http.client;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.ExtensionConfig;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientConfig implements ExtensionConfig {

    public static final int CONNECT_TIMEOUT_SECOND = 45;
    public static final int HTTP_IDLE_TIMEOUT_SECOND = 15;
    public static final int WS_IDLE_TIMEOUT_SECOND = 1200;
    public static final String KEY = "__httpClient__";
    private String userAgent = "qwe.httpclient";
    private HttpClientOptions options;
    private HttpHandlersConfig httpHandlers = new HttpHandlersConfig();
    private WebSocketHandlersConfig webSocketHandlers = new WebSocketHandlersConfig();

    HttpClientConfig() {
        this.options = new HttpClientOptions().setIdleTimeout(HTTP_IDLE_TIMEOUT_SECOND)
                                              .setIdleTimeoutUnit(TimeUnit.SECONDS)
                                              .setConnectTimeout(CONNECT_TIMEOUT_SECOND * 1000)
                                              .setTryUseCompression(true)
                                              .setWebSocketCompressionLevel(6)
                                              .setWebSocketCompressionAllowClientNoContext(true)
                                              .setWebSocketCompressionRequestServerNoContext(true)
                                              .setTryUsePerFrameWebSocketCompression(false)
                                              .setTryUsePerMessageWebSocketCompression(true);
    }

    @Override
    public String configKey() {return KEY;}

    @Override
    public JsonObject toJson() {
        return new JsonObject().put("userAgent", userAgent)
                               .put("options", options.toJson())
                               .put("httpHandlers", JsonData.tryParse(this.httpHandlers).toJson())
                               .put("webSocketHandlers", JsonData.tryParse(this.webSocketHandlers).toJson());
    }

    @JsonCreator
    static HttpClientConfig create(@JsonProperty("userAgent") String userAgent,
                                   @JsonProperty("options") JsonObject options,
                                   @JsonProperty("httpHandlers") JsonObject httpHandlers,
                                   @JsonProperty("webSocketHandlers") JsonObject webSocketHandlers) {
        return new HttpClientConfig(userAgent, new HttpClientOptions(options),
                                    JsonData.convert(Optional.ofNullable(httpHandlers).orElseGet(JsonObject::new),
                                                     HttpHandlersConfig.class),
                                    JsonData.convert(Optional.ofNullable(webSocketHandlers).orElseGet(JsonObject::new),
                                                     WebSocketHandlersConfig.class));
    }

}

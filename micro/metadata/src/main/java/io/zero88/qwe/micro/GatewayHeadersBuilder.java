package io.zero88.qwe.micro;

import java.util.Optional;

import io.github.zero88.utils.UUID64;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import lombok.Getter;

/**
 * Gateway headers builder
 */
public final class GatewayHeadersBuilder {

    /**
     * For tracking created/modified by user
     */
    public static final String X_REQUEST_USER = "X-Request-User";

    /**
     * For tracking request by service
     */
    public static final String X_REQUEST_BY = "X-Request-By";

    /**
     * Identifies the service backend URI.
     */
    public static final String X_REQUEST_URI = "X-Request-Uri";

    /**
     * Identifies the {@code method} that the client used to connect to the gateway. For example: {@code HTTP method}
     */
    public static final String X_FORWARDED_METHOD = "X-Forwarded-Method";

    /**
     * The gateway appends the last remote peer address to the {@code X-Forwarded-For} field from the incoming request.
     * A comma and space precede the appended address.
     */
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * Identifies the original host and port requested by the client to the gateway.
     */
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";

    /**
     * Identifies the original URI requested by the client to the gateway.
     */
    public static final String X_FORWARDED_URI = "X-Forwarded-Uri";

    /**
     * Identifies the protocol that the client used to connect to the gateway. For example: {@code http}/{@code https},
     * {@code ws}/{@code wss}
     */
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    /**
     * Identifies a unique identifier value that is attached to requests and messages that allow reference to a
     * particular transaction or event chain in distributed message system
     */
    public static final String X_CORRELATION_ID = "X-Correlation-ID";

    @Getter
    private final JsonObject headers;

    public GatewayHeadersBuilder(JsonObject headers) {
        this.headers = Optional.ofNullable(headers).orElseGet(JsonObject::new);
    }

    public GatewayHeadersBuilder addCorrelationId() {
        return put(X_CORRELATION_ID, genCorrelationId());
    }

    public GatewayHeadersBuilder addRequestUser(String user) {
        return put(X_REQUEST_USER, user);
    }

    public GatewayHeadersBuilder addRequestBy(String service) {
        return put(X_REQUEST_BY, service);
    }

    public GatewayHeadersBuilder addRequestURI(String path) {
        return put(X_REQUEST_URI, path);
    }

    public GatewayHeadersBuilder addForwardedURI(String path) {
        return put(X_FORWARDED_URI, path);
    }

    public GatewayHeadersBuilder addForwardedMethod(HttpMethod httpMethod) {
        return put(X_FORWARDED_METHOD, httpMethod.name());
    }

    public GatewayHeadersBuilder addForwardedProto(String proto) {
        return put(X_FORWARDED_PROTO, proto);
    }

    public GatewayHeadersBuilder addForwardedHost(String host) {
        return put(X_FORWARDED_HOST, host);
    }

    public GatewayHeadersBuilder addForwardedFor(String host) {
        return put(X_FORWARDED_FOR, host);
    }

    public HttpMethod getForwardedMethod() {
        return HttpMethod.valueOf(headers.getString(X_FORWARDED_METHOD, "UNKNOWN"));
    }

    public String getRequestURI() {
        return headers.getString(X_REQUEST_URI);
    }

    public static String genCorrelationId() {
        return UUID64.random();
    }

    private GatewayHeadersBuilder put(String key, String value) {
        headers.put(key, value);
        return this;
    }

}

package cloud.playio.qwe.micro;

import java.util.Optional;

import io.github.zero88.utils.UUID64;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import lombok.Getter;

/**
 * An object contains gateway headers
 */
public final class GatewayHeaders {

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

    public GatewayHeaders(JsonObject headers) {
        this.headers = Optional.ofNullable(headers).orElseGet(JsonObject::new);
    }

    public GatewayHeaders addCorrelationId() {
        return put(X_CORRELATION_ID, genCorrelationId());
    }

    public GatewayHeaders addRequestUser(String user) {
        return put(X_REQUEST_USER, user);
    }

    public GatewayHeaders addRequestBy(String service) {
        return put(X_REQUEST_BY, service);
    }

    public GatewayHeaders addRequestURI(String path) {
        return put(X_REQUEST_URI, path);
    }

    public GatewayHeaders addForwardedURI(String path) {
        return put(X_FORWARDED_URI, path);
    }

    public GatewayHeaders addForwardedMethod(HttpMethod httpMethod) {
        return put(X_FORWARDED_METHOD, httpMethod.name());
    }

    public GatewayHeaders addForwardedProto(String proto) {
        return put(X_FORWARDED_PROTO, proto);
    }

    public GatewayHeaders addForwardedHost(String host) {
        return put(X_FORWARDED_HOST, host);
    }

    public GatewayHeaders addForwardedFor(String host) {
        return put(X_FORWARDED_FOR, host);
    }

    public String getCorrelationId() {
        return headers.getString(X_CORRELATION_ID);
    }

    public HttpMethod getForwardedMethod() {
        return HttpMethod.valueOf(headers.getString(X_FORWARDED_METHOD, "UNKNOWN"));
    }

    public String getRequestBy() {
        return headers.getString(X_REQUEST_BY);
    }

    public String getRequestURI() {
        return headers.getString(X_REQUEST_URI);
    }

    public static String genCorrelationId() {
        return UUID64.random();
    }

    private GatewayHeaders put(String key, String value) {
        headers.put(key, value);
        return this;
    }

}

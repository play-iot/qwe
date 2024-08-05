package cloud.playio.qwe.http;

import io.vertx.core.http.HttpMethod;
import cloud.playio.qwe.eventbus.EventAction;

/**
 * HTTP Path rule to create capture path
 */
public interface HttpPathRule {

    String createCapture(HttpMethod method, EventAction action, String servicePath, String capturePath);

    default String createRegex(String path) {
        return path.replaceFirst("/:[^/]+(/?)$", "/[^/]+$1").replaceAll("/:[^/]+", "/[^/]+");
    }

    default String createRegexPathForSearch(String servicePath) {
        return servicePath + (servicePath.endsWith("/") ? "([^/]+)?" : "(/[^/]+)?");
    }

}

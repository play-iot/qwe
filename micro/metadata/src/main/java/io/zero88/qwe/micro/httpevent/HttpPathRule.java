package io.zero88.qwe.micro.httpevent;

import java.util.Arrays;
import java.util.List;

import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.event.EventAction;

/**
 * HTTP Path rule to create capture path
 */
public class HttpPathRule {

    public static final List<HttpMethod> SINGULAR_HTTP_METHODS = Arrays.asList(HttpMethod.GET, HttpMethod.DELETE,
                                                                               HttpMethod.PUT, HttpMethod.PATCH);

    public boolean isSingular(HttpMethod method) {
        return SINGULAR_HTTP_METHODS.contains(method);
    }

    public boolean hasBody(HttpMethod method) {
        return HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method) ||
               HttpMethod.TRACE.equals(method);
    }

    public String createCapture(HttpMethod method, EventAction action, String servicePath, String capturePath) {
        boolean isSingular = isSingular(method) && action != EventAction.GET_LIST || action == EventAction.GET_ONE;
        return Strings.isBlank(servicePath) || isSingular ? capturePath : servicePath;
    }

    public String createRegex(String path) {
        //FIXME check with forward url
        return path.replaceFirst("/:[^/]+(/?)$", "/.+$1").replaceAll("/:[^/]+", "/[^/]+");
    }

    public String createRegexPathForSearch(String servicePath) {
        return servicePath + (servicePath.endsWith("/") ? "([^/]+)?" : "(/[^/]+)?");
    }

}

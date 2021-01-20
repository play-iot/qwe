package io.github.zero88.qwe.utils;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.http.HttpMethod;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpMethods {

    private static final List<HttpMethod> SINGULAR_HTTP_METHODS = Arrays.asList(HttpMethod.GET, HttpMethod.DELETE,
                                                                                HttpMethod.PUT, HttpMethod.PATCH);

    public static boolean isSingular(HttpMethod method) {
        return SINGULAR_HTTP_METHODS.contains(method);
    }

    public static boolean hasBody(HttpMethod method) {
        return HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method) ||
               HttpMethod.TRACE.equals(method);
    }

}

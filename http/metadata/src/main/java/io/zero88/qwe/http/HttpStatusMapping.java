package io.zero88.qwe.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import io.github.zero88.exceptions.HiddenException;
import io.zero88.qwe.cluster.ClusterException;
import io.zero88.qwe.exceptions.BeingUsedException;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.ConflictException;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.HttpException;
import io.zero88.qwe.exceptions.SecurityException;
import io.zero88.qwe.exceptions.SecurityException.AuthenticationException;
import io.zero88.qwe.exceptions.SecurityException.InsufficientPermissionError;
import io.zero88.qwe.exceptions.TimeoutException;
import io.zero88.qwe.micro.ServiceNotFoundException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @see io.github.zero88.exceptions.ErrorCode
 * @see ErrorCode
 */
//    TODO need more update
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpStatusMapping {

    private static final Map<String, HttpResponseStatus> STATUS_ERROR = init();
    private static final Map<String, Map<HttpMethod, HttpResponseStatus>> STATUS_METHOD_ERROR = initMethod();

    private static Map<String, Map<HttpMethod, HttpResponseStatus>> initMethod() {
        Map<String, Map<HttpMethod, HttpResponseStatus>> map = new HashMap<>();

        Map<HttpMethod, HttpResponseStatus> notFound = new HashMap<>();
        HttpMethod.values().forEach(method -> notFound.put(method, HttpResponseStatus.GONE));
        notFound.put(HttpMethod.GET, HttpResponseStatus.NOT_FOUND);
        map.put(ErrorCode.NOT_FOUND.code(), notFound);

        return Collections.unmodifiableMap(map);
    }

    private static Map<String, HttpResponseStatus> init() {
        Map<String, HttpResponseStatus> map = new HashMap<>();
        map.put(ErrorCode.INVALID_ARGUMENT.code(), HttpResponseStatus.BAD_REQUEST);
        map.put(HttpException.HTTP_ERROR.code(), HttpResponseStatus.BAD_REQUEST);

        map.put(ServiceNotFoundException.CODE.code(), HttpResponseStatus.NOT_FOUND);
        map.put(ErrorCode.ALREADY_EXIST.code(), HttpResponseStatus.UNPROCESSABLE_ENTITY);
        map.put(BeingUsedException.CODE.code(), HttpResponseStatus.UNPROCESSABLE_ENTITY);

        map.put(ConflictException.CODE.code(), HttpResponseStatus.CONFLICT);
        map.put(ErrorCode.UNSUPPORTED.code(), HttpResponseStatus.CONFLICT);

        map.put(AuthenticationException.CODE.code(), HttpResponseStatus.UNAUTHORIZED);
        map.put(SecurityException.CODE.code(), HttpResponseStatus.FORBIDDEN);
        map.put(InsufficientPermissionError.CODE.code(), HttpResponseStatus.FORBIDDEN);

        map.put(ErrorCode.EVENT_ERROR.code(), HttpResponseStatus.SERVICE_UNAVAILABLE);
        map.put(ClusterException.CODE.code(), HttpResponseStatus.SERVICE_UNAVAILABLE);

        map.put(TimeoutException.CODE.code(), HttpResponseStatus.REQUEST_TIMEOUT);
        return Collections.unmodifiableMap(map);
    }

    public static HttpResponseStatus success(HttpMethod method) {
        if (HttpMethod.DELETE == method) {
            return HttpResponseStatus.NO_CONTENT;
        }
        if (HttpMethod.POST == method) {
            return HttpResponseStatus.CREATED;
        }
        return HttpResponseStatus.OK;
    }

    public static HttpResponseStatus error(HttpMethod method, CarlException exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof HiddenException) {
            return error(method, ((HiddenException) cause).errorCode());
        }
        return error(method, exception.errorCode());
    }

    public static HttpResponseStatus error(HttpMethod method, io.github.zero88.exceptions.ErrorCode errorCode) {
        HttpResponseStatus status = STATUS_ERROR.get(errorCode.code());
        if (Objects.nonNull(status)) {
            return status;
        }
        return STATUS_METHOD_ERROR.getOrDefault(errorCode.code(), new HashMap<>())
                                  .getOrDefault(method, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public static io.github.zero88.exceptions.ErrorCode error(HttpMethod method, int code) {
        return error(method, HttpResponseStatus.valueOf(code));
    }

    public static io.github.zero88.exceptions.ErrorCode error(HttpMethod method, HttpResponseStatus statusCode) {
        return STATUS_METHOD_ERROR.entrySet()
                                  .stream()
                                  .filter(entry -> entry.getValue()
                                                        .entrySet()
                                                        .stream()
                                                        .anyMatch(
                                                            e -> e.getKey() == method && e.getValue() == statusCode))
                                  .map(Entry::getKey)
                                  .findFirst()
                                  .map(ErrorCode::parse)
                                  .map(io.github.zero88.exceptions.ErrorCode.class::cast)
                                  .orElseGet(() -> STATUS_ERROR.entrySet()
                                                               .stream()
                                                               .filter(entry -> entry.getValue() == statusCode)
                                                               .map(Entry::getKey)
                                                               .findFirst()
                                                               .map(ErrorCode::parse)
                                                               .map(io.github.zero88.exceptions.ErrorCode.class::cast)
                                                               .orElse(ErrorCode.UNKNOWN_ERROR));
    }

}

package io.zero88.qwe.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

import lombok.Getter;
import lombok.NonNull;

public final class HttpException extends CarlException {

    public static final ErrorCode HTTP_ERROR = ErrorCode.parse("HTTP_ERROR");

    @Getter
    private HttpResponseStatus statusCode = null;

    public HttpException(String message, Throwable e) {
        super(HTTP_ERROR, message, e);
    }

    public HttpException(int statusCode, String message) {
        this(HttpResponseStatus.valueOf(statusCode), message);
    }

    public HttpException(@NonNull HttpResponseStatus statusCode, String message) {
        super(HTTP_ERROR, message);
        this.statusCode = statusCode;
    }

    public HttpException(String message) { this(message, null); }

    public HttpException(Throwable e)    { this(null, e); }

    public HttpException(int statusCode, String message, Throwable e) {
        super(HTTP_ERROR, message, e);
        this.statusCode = HttpResponseStatus.valueOf(statusCode);
    }

    public static HttpException badRequest(String message) {
        return new HttpException(HttpResponseStatus.BAD_REQUEST.code(), message);
    }

    public static HttpException forbidden() {
        return new HttpException(HttpResponseStatus.FORBIDDEN.code(),
                                 "You don't have permission to perform the action.");
    }

}

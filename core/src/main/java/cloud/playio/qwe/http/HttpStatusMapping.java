package cloud.playio.qwe.http;

import io.github.zero88.exceptions.HiddenException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.QWEException;

/**
 * @see ErrorCode
 */
public interface HttpStatusMapping {

    default HttpResponseStatus success(HttpMethod method) {
        if (HttpMethod.DELETE == method) {
            return HttpResponseStatus.NO_CONTENT;
        }
        if (HttpMethod.POST == method) {
            return HttpResponseStatus.CREATED;
        }
        return HttpResponseStatus.OK;
    }

    default HttpResponseStatus error(HttpMethod method, QWEException exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof HiddenException) {
            return error(method, ErrorCode.wrap(((HiddenException) cause).errorCode()));
        }
        return error(method, exception.errorCode());
    }

    HttpResponseStatus error(HttpMethod method, io.github.zero88.exceptions.ErrorCode errorCode);

    default ErrorCode error(HttpMethod method, int code) {
        return error(method, HttpResponseStatus.valueOf(code));
    }

    ErrorCode error(HttpMethod method, HttpResponseStatus status);

}

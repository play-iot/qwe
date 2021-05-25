package io.zero88.qwe.http.server.handler;

import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public final class ApiExceptionHandler implements HttpResponseWriter<Throwable> {

    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {
        ErrorMessage errorMessage = ErrorMessage.parse(result);
        response.setStatusCode(HttpStatusMapping.error(request.method(), errorMessage.getThrowable()).code())
                .end(HttpUtils.prettify(errorMessage, request));
    }

}

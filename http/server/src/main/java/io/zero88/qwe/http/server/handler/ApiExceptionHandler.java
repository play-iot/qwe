package io.zero88.qwe.http.server.handler;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.http.HttpStatusMappingLoader;
import io.zero88.qwe.http.HttpUtils;

public final class ApiExceptionHandler implements HttpResponseWriter<Throwable> {

    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {
        ErrorMessage errorMessage = ErrorMessage.parse(result);
        response.setStatusCode(
                    HttpStatusMappingLoader.getInstance().get().error(request.method(), errorMessage.getThrowable()).code())
                .end(HttpUtils.prettify(errorMessage, request));
    }

}

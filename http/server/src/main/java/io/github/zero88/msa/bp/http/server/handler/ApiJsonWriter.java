package io.github.zero88.msa.bp.http.server.handler;

import java.util.Objects;

import io.github.zero88.msa.bp.http.HttpUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import com.zandero.rest.writer.HttpResponseWriter;

public final class ApiJsonWriter<T> implements HttpResponseWriter<T> {

    @Override
    public void write(T result, HttpServerRequest request, HttpServerResponse response) {
        if (Objects.isNull(result)) {
            response.end();
        } else {
            response.end(HttpUtils.prettify(result, request));
        }
    }

}

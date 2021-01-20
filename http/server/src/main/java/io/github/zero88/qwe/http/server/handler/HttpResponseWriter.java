package io.github.zero88.qwe.http.server.handler;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface HttpResponseWriter<T> {

    void write(T result, HttpServerRequest request, HttpServerResponse response);

}

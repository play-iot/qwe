package io.zero88.qwe.http.client.handler;

import io.vertx.core.Handler;
import io.vertx.reactivex.core.http.HttpClientResponse;

/**
 * Represents for handling a response binary data that includes {@code HTTP Response header}, {@code HTTP Response
 * status}, {@code HTTP Response binary}.
 * <p>
 * It is suite for downloading data
 */
public class HttpResponseBinaryHandler implements Handler<HttpClientResponse> {

    @Override
    public void handle(HttpClientResponse event) {

    }

}

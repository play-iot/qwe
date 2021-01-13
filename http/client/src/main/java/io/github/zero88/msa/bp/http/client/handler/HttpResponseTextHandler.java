package io.github.zero88.msa.bp.http.client.handler;

import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.reactivex.SingleEmitter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents for handling a lightweight {@code HTTP response message} that includes {@code HTTP Response header},
 * {@code HTTP Response status}, {@code HTTP Response body}
 *
 * @see HttpResponseTextBodyHandler
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-6">HTTP Response status</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-7">HTTP Response header</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpResponseTextHandler<T extends HttpResponseTextBodyHandler>
    implements Handler<AsyncResult<HttpClientResponse>> {

    @NonNull
    private final SingleEmitter<ResponseData> emitter;
    @NonNull
    private final Class<T> bodyHandlerClass;
    private final boolean swallowError;

    public static <H extends HttpResponseTextBodyHandler> Handler<AsyncResult<HttpClientResponse>> create(
        @NonNull SingleEmitter<ResponseData> emitter, boolean swallowError, @NonNull Class<H> bodyHandlerClass) {
        return new HttpResponseTextHandler<>(emitter, bodyHandlerClass, swallowError);
    }

    @Override
    public void handle(AsyncResult<HttpClientResponse> result) {
        if (result.succeeded()) {
            final HttpClientResponse response = result.result();
            T bodyHandler = HttpResponseTextBodyHandler.create(response, emitter, swallowError, bodyHandlerClass);
            response.bodyHandler(bodyHandler).exceptionHandler(emitter::onError);
        } else {
            emitter.onError(result.cause());
        }
    }

}

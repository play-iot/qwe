package io.zero88.qwe.http.client.handler;

import java.util.Objects;
import java.util.function.BiFunction;

import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.http.HttpUtils.HttpHeaderUtils;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.http.HttpHeaders;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpClientRequest;

import lombok.NonNull;

/**
 * Represents for a composer that prepare and compose {@code HTTP Request Message} to send it to any host
 * <p>
 * {@code HTTP Request Message} includes {@code HTTP Request Header}, {@code HTTP Request payload}
 *
 * @apiNote Never close {@code HTTPClientRequest} in this function
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-5">HTTP Request header</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.3">HTTP Request payload</a>
 */
//TODO Optimize header
public interface HttpRequestMessageComposer extends BiFunction<HttpClientRequest, RequestData, HttpClientRequest> {

    HttpRequestMessageComposer DEFAULT = new HttpRequestMessageComposer() {};

    @SuppressWarnings("unchecked")
    static <T extends HttpRequestMessageComposer> T create(Class<T> writerClass) {
        return Objects.isNull(writerClass) || HttpRequestMessageComposer.class.equals(writerClass)
               ? (T) DEFAULT
               : ReflectionClass.createObject(writerClass);
    }

    @Override
    default HttpClientRequest apply(@NonNull HttpClientRequest request, RequestData reqData) {
        if (Objects.isNull(reqData)) {
            return request;
        }
        if (!reqData.headers().isEmpty()) {
            request.headers()
                   .setAll(MultiMap.newInstance(HttpHeaderUtils.deserializeHeaders(reqData.headers())))
                   .remove(HttpHeaders.ACCEPT_ENCODING)
                   .remove(HttpHeaders.CONTENT_LENGTH);
        }
        if (Objects.nonNull(reqData.body()) && !reqData.body().isEmpty()) {
            final io.vertx.core.buffer.Buffer buffer = reqData.body().toBuffer();
            request.putHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(buffer.length()));
            request.write(Buffer.newInstance(buffer));
        }
        return request;
    }

}

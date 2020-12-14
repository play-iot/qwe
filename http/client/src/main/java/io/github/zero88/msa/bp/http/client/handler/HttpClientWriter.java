package io.github.zero88.msa.bp.http.client.handler;

import java.util.Objects;
import java.util.function.BiFunction;

import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.http.HttpUtils.HttpHeaderUtils;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;

import lombok.NonNull;

/**
 * Represents for {@code HTTP client} write data (header/body/cookie) into request before actual sending to specific
 * server.
 * <b>Notice:</b> Never close {@code HTTPClientRequest} in this function
 */
//TODO Optimize header
public interface HttpClientWriter extends BiFunction<HttpClientRequest, RequestData, HttpClientRequest> {

    HttpClientWriter DEFAULT = new HttpClientWriter() {};

    @SuppressWarnings("unchecked")
    static <T extends HttpClientWriter> T create(Class<T> writerClass) {
        return Objects.isNull(writerClass) || HttpClientWriter.class.equals(writerClass)
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
                   .setAll(HttpHeaderUtils.deserializeHeaders(reqData.headers()))
                   .remove(HttpHeaders.ACCEPT_ENCODING)
                   .remove(HttpHeaders.CONTENT_LENGTH);
        }
        if (Objects.nonNull(reqData.body()) && !reqData.body().isEmpty()) {
            final Buffer buffer = reqData.body().toBuffer();
            request.putHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(buffer.length()));
            request.write(buffer);
        }
        return request;
    }

}

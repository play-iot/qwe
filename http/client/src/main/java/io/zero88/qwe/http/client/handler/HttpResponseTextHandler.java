package io.zero88.qwe.http.client.handler;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.HttpUtils.HttpHeaderUtils;

import lombok.RequiredArgsConstructor;

/**
 * Represents for handling a lightweight {@code HTTP response message} that includes {@code HTTP Response header},
 * {@code HTTP Response status}, {@code HTTP Response body}
 *
 * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3">HTTP Response Body</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-6">HTTP Response status</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-7">HTTP Response header</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#body_2">HTTP Response body</a>
 */
@RequiredArgsConstructor
public abstract class HttpResponseTextHandler implements Function<HttpClientResponse, Future<ResponseData>> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean swallowError;

    @SuppressWarnings("unchecked")
    public static <T extends HttpResponseTextHandler> T create(boolean swallowError, Class<T> bodyHandlerClass) {
        if (Objects.isNull(bodyHandlerClass) || HttpResponseTextHandler.class.equals(bodyHandlerClass)) {
            return (T) new HttpResponseTextHandler(swallowError) {};
        }
        return ReflectionClass.createObject(bodyHandlerClass, Collections.singletonMap(boolean.class, swallowError));
    }

    @Override
    public Future<ResponseData> apply(HttpClientResponse response) {
        return response.body().flatMap(buffer -> {
            final JsonObject body = tryParse(response, buffer);
            final int status = response.statusCode();
            if (!swallowError && status >= 400) {
                ErrorCode code = HttpStatusMapping.error(response.request().getMethod(), status);
                return Future.failedFuture(new CarlException(code, body.encode()));
            }
            return Future.succeededFuture(
                new ResponseData().setStatus(status).setHeaders(overrideHeader(response)).setBody(body));
        });
    }

    private JsonObject overrideHeader(HttpClientResponse response) {
        return HttpHeaderUtils.serializeHeaders(response.headers())
                              .put(HttpHeaders.CONTENT_TYPE.toString(), HttpUtils.JSON_UTF8_CONTENT_TYPE);
    }

    protected JsonObject tryParse(HttpClientResponse response, Buffer buffer) {
        String contentType = response.getHeader(HttpHeaders.CONTENT_TYPE);
        final HttpMethod method = response.request().getMethod();
        final String uri = response.request().absoluteURI();
        final boolean isError = response.statusCode() >= 400;
        if (Strings.isNotBlank(contentType) && contentType.contains("json")) {
            logger.info("Try parsing Json data from {}::{}", method, uri);
            return JsonData.tryParse(buffer, true, isError).toJson();
        }
        logger.warn("Try parsing Json in ambiguous case from {}::{}", method, uri);
        return JsonData.tryParse(buffer, false, isError).toJson();
    }

}

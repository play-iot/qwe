package io.github.zero88.msa.bp.http.client.handler;

import java.util.Collections;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.github.zero88.msa.bp.http.HttpUtils.HttpHeaderUtils;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpClientResponse;

import lombok.NonNull;
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
public abstract class HttpResponseTextHandler implements Function<HttpClientResponse, Single<ResponseData>> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @NonNull
    private final boolean swallowError;

    @SuppressWarnings("unchecked")
    public static <T extends HttpResponseTextHandler> T create(boolean swallowError, Class<T> bodyHandlerClass) {
        if (Objects.isNull(bodyHandlerClass) || HttpResponseTextHandler.class.equals(bodyHandlerClass)) {
            return (T) new HttpResponseTextHandler(swallowError) {};
        }
        return ReflectionClass.createObject(bodyHandlerClass, Collections.singletonMap(boolean.class, swallowError));
    }

    @Override
    public Single<ResponseData> apply(HttpClientResponse response) throws Exception {
        return response.rxBody().flatMap(buffer -> {
            final JsonObject body = tryParse(response, buffer);
            final int status = response.statusCode();
            if (!swallowError && status >= 400) {
                ErrorCode code = HttpStatusMapping.error(response.request().getMethod(), status);
                return Single.error(new BlueprintException(code, body.encode()));
            }
            return Single.just(new ResponseData().setStatus(status).setHeaders(overrideHeader(response)).setBody(body));
        });
    }

    private JsonObject overrideHeader(HttpClientResponse response) {
        return HttpHeaderUtils.serializeHeaders(response.headers().getDelegate())
                              .put(HttpHeaders.CONTENT_TYPE.toString(), HttpUtils.JSON_UTF8_CONTENT_TYPE);
    }

    protected JsonObject tryParse(HttpClientResponse response, Buffer buffer) {
        String contentType = response.getHeader(HttpHeaders.CONTENT_TYPE);
        final HttpMethod method = response.request().getMethod();
        final String uri = response.request().absoluteURI();
        final boolean isError = response.statusCode() >= 400;
        if (Strings.isNotBlank(contentType) && contentType.contains("json")) {
            logger.info("Try parsing Json data from {}::{}", method, uri);
            return JsonData.tryParse(buffer.getDelegate(), true, isError).toJson();
        }
        logger.warn("Try parsing Json in ambiguous case from {}::{}", method, uri);
        return JsonData.tryParse(buffer.getDelegate(), false, isError).toJson();
    }

}

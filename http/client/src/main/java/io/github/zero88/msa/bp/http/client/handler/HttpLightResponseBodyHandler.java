package io.github.zero88.msa.bp.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.github.zero88.msa.bp.http.HttpUtils.HttpHeaderUtils;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.reactivex.SingleEmitter;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents for handler {@code HTTP Response}
 */
@RequiredArgsConstructor
public abstract class HttpLightResponseBodyHandler implements Handler<Buffer> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @NonNull
    private final HttpClientResponse response;
    @NonNull
    private final SingleEmitter<ResponseData> emitter;
    private final boolean swallowError;

    @SuppressWarnings("unchecked")
    public static <T extends HttpLightResponseBodyHandler> T create(HttpClientResponse response,
                                                                    SingleEmitter<ResponseData> emitter,
                                                                    boolean swallowError, Class<T> bodyHandlerClass) {
        if (Objects.isNull(bodyHandlerClass) || HttpLightResponseBodyHandler.class.equals(bodyHandlerClass)) {
            return (T) new HttpLightResponseBodyHandler(response, emitter, swallowError) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(HttpClientResponse.class, response);
        params.put(SingleEmitter.class, emitter);
        params.put(boolean.class, swallowError);
        return ReflectionClass.createObject(bodyHandlerClass, params);
    }

    @Override
    public void handle(Buffer buffer) {
        final JsonObject body = tryParse(buffer);
        if (!swallowError && response.statusCode() >= 400) {
            ErrorCode code = HttpStatusMapping.error(response.request().method(), response.statusCode());
            emitter.onError(new BlueprintException(code, body.encode()));
            return;
        }
        emitter.onSuccess(
            new ResponseData().setStatus(response.statusCode()).setHeaders(overrideHeader(response)).setBody(body));
    }

    private JsonObject overrideHeader(HttpClientResponse response) {
        return HttpHeaderUtils.serializeHeaders(response.headers())
                              .put(HttpHeaders.CONTENT_TYPE.toString(), HttpUtils.JSON_UTF8_CONTENT_TYPE);
    }

    protected JsonObject tryParse(Buffer buffer) {
        String contentType = response.getHeader(HttpHeaders.CONTENT_TYPE);
        final HttpMethod method = response.request().method();
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

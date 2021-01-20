package io.github.zero88.qwe.http.server.handler;

import java.util.Objects;

import io.github.zero88.qwe.dto.msg.ResponseData;
import io.github.zero88.qwe.http.HttpUtils;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import com.zandero.rest.writer.HttpResponseWriter;

// FIXME: We will find better way to handle the serialization of ResponseData
@Deprecated
public class ResponseDataWriter implements HttpResponseWriter<ResponseData> {

    private static String SERIALIZATION_KEY = "message";

    public static ResponseData serializeResponseData(ResponseData message) {
        return message.setBody(new JsonObject().put(SERIALIZATION_KEY, message.body())).setHeaders(new JsonObject());
    }

    public static ResponseData serializeResponseData(String message) {
        return new ResponseData().setBody(new JsonObject().put(SERIALIZATION_KEY, message));
    }

    public static void serializeResponseData(ResponseData responseData, String message) {
        responseData.setBody(new JsonObject().put(SERIALIZATION_KEY, message));
    }

    @Override
    public void write(ResponseData result, HttpServerRequest request, HttpServerResponse response) {
        Object message = deSerializeResponseBody(result.body());
        response.setStatusCode(result.getStatus().code());
        response.headers().addAll(HttpUtils.HttpHeaderUtils.deserializeHeaders(result.headers()));
        response.putHeader(HttpHeaders.CONTENT_TYPE, HttpUtils.JSON_UTF8_CONTENT_TYPE);

        if (Objects.isNull(message)) {
            response.end();
        } else {
            response.end(message.toString());
        }
    }

    private Object deSerializeResponseBody(JsonObject body) {
        return body.getValue(SERIALIZATION_KEY);
    }

}

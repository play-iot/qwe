package io.zero88.qwe.http.server.converter;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.http.HttpException;
import io.zero88.qwe.http.HttpStatusMappingLoader;
import io.zero88.qwe.http.server.handler.ResponseDataWriter;

public class ResponseDataConverter {

    public static ResponseData convert(HttpMethod httpMethod, Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            return ResponseDataWriter.serializeResponseData(
                                         new JsonObject().put("error", httpException.getMessage()).encode())
                                     .setStatus(httpException.getStatusCode().code());
        } else if (e instanceof QWEException) {
            QWEException ex = (QWEException) e;
            HttpResponseStatus responseStatus = HttpStatusMappingLoader.getInstance()
                                                                       .get()
                                                                       .error(httpMethod, ex.errorCode());
            return ResponseDataWriter.serializeResponseData(new JsonObject().put("error", ex.getMessage()).encode())
                                     .setStatus(responseStatus.code());
        } else {
            return ResponseDataWriter.serializeResponseData(new JsonObject().put("error", e.getMessage()).encode())
                                     .setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ResponseData convert(Throwable e) {
        return convert(HttpMethod.GET, e);
    }

}

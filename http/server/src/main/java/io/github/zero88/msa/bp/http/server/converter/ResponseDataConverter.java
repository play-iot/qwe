package io.github.zero88.msa.bp.http.server.converter;

import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.HttpException;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.server.handler.ResponseDataWriter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class ResponseDataConverter {

    public static ResponseData convert(HttpMethod httpMethod, Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            return ResponseDataWriter.serializeResponseData(
                new JsonObject().put("error", httpException.getMessage()).encode())
                                     .setStatus(httpException.getStatusCode().code());
        } else if (e instanceof BlueprintException) {
            BlueprintException ex = (BlueprintException) e;
            HttpResponseStatus responseStatus = HttpStatusMapping.error(httpMethod, ex.errorCode());
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

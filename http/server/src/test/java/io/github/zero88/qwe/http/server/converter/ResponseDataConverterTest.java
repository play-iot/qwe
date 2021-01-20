package io.github.zero88.qwe.http.server.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.qwe.dto.msg.ResponseData;
import io.github.zero88.qwe.exceptions.HttpException;
import io.github.zero88.qwe.exceptions.NotFoundException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;

public class ResponseDataConverterTest {

    @Test
    public void testConvertHttpExceptionToResponseData() {
        int statusCode = HttpResponseStatus.FORBIDDEN.code();
        String message = "You are not Authorized to perform this action";

        HttpException httpException = new HttpException(statusCode, message);
        ResponseData responseData = ResponseDataConverter.convert(httpException);

        Assertions.assertEquals(responseData.getStatus().code(), statusCode);
        Assertions.assertEquals(responseData.body().getString("message"),
                                new JsonObject().put("error", message).encode());
    }

    @Test
    public void testConvertNotFoundExceptionToResponseData() {
        String message = "Not found";

        NotFoundException notFoundException = new NotFoundException(message);
        ResponseData responseData = ResponseDataConverter.convert(notFoundException);

        Assertions.assertEquals(responseData.getStatus().code(), HttpResponseStatus.NOT_FOUND.code());
        Assertions.assertEquals(responseData.body().getString("message"),
                                new JsonObject().put("error", message).encode());
    }

    @Test
    public void testConvertThrowableToResponseData() {
        String message = "Something went wrong, internal server error";

        Throwable throwable = new Throwable("Something went wrong, internal server error");
        ResponseData responseData = ResponseDataConverter.convert(throwable);

        Assertions.assertEquals(responseData.getStatus().code(), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        Assertions.assertEquals(responseData.body().getString("message"),
                                new JsonObject().put("error", message).encode());
    }

}

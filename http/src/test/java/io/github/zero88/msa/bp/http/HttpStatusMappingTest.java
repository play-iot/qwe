package io.github.zero88.msa.bp.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.bp.cluster.ClusterException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.HttpException;
import io.github.zero88.msa.bp.exceptions.ServiceException;
import io.github.zero88.utils.Strings;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;

public class HttpStatusMappingTest {

    @Test
    public void test_success_ok() {
        Assertions.assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.GET));
        Assertions.assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.PUT));
        Assertions.assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.PATCH));
        Assertions.assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.HEAD));
        Assertions.assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.OPTIONS));
    }

    @Test
    public void test_success_post() {
        Assertions.assertEquals(HttpResponseStatus.CREATED, HttpStatusMapping.success(HttpMethod.POST));
    }

    @Test
    public void test_success_delete() {
        Assertions.assertEquals(HttpResponseStatus.NO_CONTENT, HttpStatusMapping.success(HttpMethod.DELETE));
    }

    @Test
    public void test_error_bad_request() {
        Assertions.assertEquals(HttpResponseStatus.BAD_REQUEST,
                                HttpStatusMapping.error(HttpMethod.DELETE, HttpException.HTTP_ERROR));
        Assertions.assertEquals(HttpResponseStatus.BAD_REQUEST,
                                HttpStatusMapping.error(HttpMethod.POST, ErrorCode.INVALID_ARGUMENT));
    }

    @Test
    public void test_error_gone() {
        HttpMethod.values()
                  .parallelStream()
                  .filter(method -> HttpMethod.GET != method)
                  .forEach(method -> Assertions.assertEquals(HttpResponseStatus.GONE,
                                                             HttpStatusMapping.error(method, ErrorCode.NOT_FOUND),
                                                             "HTTP Method: " + method));
    }

    @Test
    public void test_error_not_found() {
        Assertions.assertEquals(HttpResponseStatus.NOT_FOUND,
                                HttpStatusMapping.error(HttpMethod.GET, ErrorCode.NOT_FOUND));
    }

    @Test
    public void test_error_internal_server_error() {
        Assertions.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                                HttpStatusMapping.error(HttpMethod.DELETE, ErrorCode.UNKNOWN_ERROR));
        Assertions.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                                HttpStatusMapping.error(HttpMethod.GET, ErrorCode.SERVICE_ERROR));
    }

    @Test
    public void test_error_service_unavailable() {
        Map<ErrorCode, List<HttpMethod>> test = new HashMap<>();
        test.put(ErrorCode.EVENT_ERROR, HttpMethod.values());
        test.put(ClusterException.CODE, HttpMethod.values());
        test.entrySet()
            .stream()
            .parallel()
            .forEach(e -> e.getValue()
                           .parallelStream()
                           .forEach(method -> Assertions.assertEquals(HttpResponseStatus.SERVICE_UNAVAILABLE,
                                                                      HttpStatusMapping.error(method, e.getKey()),
                                                                      Strings.format("Method: {0} | Code: {1}", method,
                                                                                     e.getKey()))));
    }

    @Test
    public void test_error_by_exception_without_hidden() {
        Assertions.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                                HttpStatusMapping.error(HttpMethod.GET, new ServiceException("hey")));
    }

    @Test
    public void test_error_by_exception_with_hidden() {
        ServiceException t = new ServiceException("Hey", new HiddenException(ClusterException.CODE, "xx", null));
        Assertions.assertEquals(HttpResponseStatus.SERVICE_UNAVAILABLE, HttpStatusMapping.error(HttpMethod.GET, t));
    }

}

package io.github.zero88.msa.blueprint.exceptions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.blueprint.exceptions.converter.HttpStatusMapping;
import io.github.zero88.utils.Strings;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;

public class HttpStatusMappingTest {

    @Test
    public void test_success_ok() {
        assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.GET));
        assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.PUT));
        assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.PATCH));
        assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.HEAD));
        assertEquals(HttpResponseStatus.OK, HttpStatusMapping.success(HttpMethod.OPTIONS));
    }

    @Test
    public void test_success_post() {
        assertEquals(HttpResponseStatus.CREATED, HttpStatusMapping.success(HttpMethod.POST));
    }

    @Test
    public void test_success_delete() {
        assertEquals(HttpResponseStatus.NO_CONTENT, HttpStatusMapping.success(HttpMethod.DELETE));
    }

    @Test
    public void test_error_bad_request() {
        assertEquals(HttpResponseStatus.BAD_REQUEST,
                     HttpStatusMapping.error(HttpMethod.DELETE, ErrorCode.HTTP_ERROR));
        assertEquals(HttpResponseStatus.BAD_REQUEST,
                     HttpStatusMapping.error(HttpMethod.POST, ErrorCode.INVALID_ARGUMENT));
    }

    @Test
    public void test_error_gone() {
        Arrays.stream(HttpMethod.values())
              .parallel()
              .filter(method -> HttpMethod.GET != method)
              .forEach(method -> assertEquals("HTTP Method: " + method, HttpResponseStatus.GONE,
                                              HttpStatusMapping.error(method, ErrorCode.NOT_FOUND)));
    }

    @Test
    public void test_error_not_found() {
        assertEquals(HttpResponseStatus.NOT_FOUND, HttpStatusMapping.error(HttpMethod.GET, ErrorCode.NOT_FOUND));
    }

    @Test
    public void test_error_internal_server_error() {
        assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                     HttpStatusMapping.error(HttpMethod.DELETE, ErrorCode.UNKNOWN_ERROR));
        assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                     HttpStatusMapping.error(HttpMethod.GET, ErrorCode.SERVICE_ERROR));
    }

    @Test
    public void test_error_service_unavailable() {
        Map<ErrorCode, List<HttpMethod>> test = new HashMap<>();
        test.put(ErrorCode.EVENT_ERROR, Arrays.asList(HttpMethod.values()));
        test.put(ErrorCode.CLUSTER_ERROR, Arrays.asList(HttpMethod.values()));
        test.entrySet()
            .stream()
            .parallel()
            .forEach(entry -> entry.getValue()
                                   .parallelStream()
                                   .forEach(method -> assertEquals(
                                       Strings.format("Method: {0} | Code: {1}", method, entry.getKey()),
                                       HttpResponseStatus.SERVICE_UNAVAILABLE,
                                       HttpStatusMapping.error(method, entry.getKey()))));
    }

    @Test
    public void test_error_by_exception_without_hidden() {
        assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                     HttpStatusMapping.error(HttpMethod.GET, new ServiceException("hey")));
    }

    @Test
    public void test_error_by_exception_with_hidden() {
        ServiceException t = new ServiceException("Hey", new HiddenException(ErrorCode.CLUSTER_ERROR, "xx", null));
        assertEquals(HttpResponseStatus.SERVICE_UNAVAILABLE, HttpStatusMapping.error(HttpMethod.GET, t));
    }

}

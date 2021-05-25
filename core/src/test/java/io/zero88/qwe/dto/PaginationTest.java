package io.zero88.qwe.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.dto.jpa.Pagination;
import io.vertx.core.json.JsonObject;

public class PaginationTest {

    @Test
    public void test_build_without_value() {
        Pagination pagination = Pagination.builder().build();
        Assertions.assertEquals(1, pagination.getPage());
        Assertions.assertEquals(20, pagination.getPerPage());
    }

    @Test
    public void test_build_with_value() {
        Pagination pagination = Pagination.builder().page(5).perPage(15).build();
        Assertions.assertEquals(5, pagination.getPage());
        Assertions.assertEquals(15, pagination.getPerPage());
    }

    @Test
    public void test_build_with_per_page_greater() {
        Pagination pagination = Pagination.builder().perPage(50).build();
        Assertions.assertEquals(1, pagination.getPage());
        Assertions.assertEquals(20, pagination.getPerPage());
    }

    @Test
    public void test_build_with_per_page_equals_zero() {
        Pagination pagination = Pagination.builder().perPage(0).build();
        Assertions.assertEquals(1, pagination.getPage());
        Assertions.assertEquals(20, pagination.getPerPage());
    }

    @Test
    public void test_build_with_page_equals_zero() {
        Pagination pagination = Pagination.builder().page(0).build();
        Assertions.assertEquals(1, pagination.getPage());
        Assertions.assertEquals(20, pagination.getPerPage());
    }

    @Test
    public void test_from_json() {
        final JsonObject init = new JsonObject().put("_page", 5).put("_per_page", 10);
        final Pagination pagination = init.mapTo(Pagination.class);
        Assertions.assertEquals(5, pagination.getPage());
        Assertions.assertEquals(10, pagination.getPerPage());
    }

}

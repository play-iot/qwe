package io.github.zero88.qwe.http.server.rest;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.http.server.mock.MockApiDefinition.MockRestEventApi;
import io.github.zero88.qwe.http.server.rest.api.RestEventApi;

public class RestEventApisCreatorTest {

    @Test
    public void test_no_register_data() {
        Assertions.assertThrows(InitializerError.class, () -> new RestEventApisCreator().validate());
    }

    @Test
    public void test_register_null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> new RestEventApisCreator().register((Class<RestEventApi>) null));
    }

    @Test
    public void test_register_one_api() {
        Set<Class<? extends RestEventApi>> validate = new RestEventApisCreator().register(
            MockRestEventApi.class).validate();
        Assertions.assertEquals(1, validate.size());
    }

    @Test
    public void test_register_many_same_api() {
        Set<Class<? extends RestEventApi>> validate = new RestEventApisCreator().register(
            MockRestEventApi.class, MockRestEventApi.class).validate();
        Assertions.assertEquals(1, validate.size());
    }

}

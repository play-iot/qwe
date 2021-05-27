package io.zero88.qwe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.exceptions.QWEException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HasConfigTest {

    private MockHasConfig hasConfig;

    @BeforeEach
    public void setUp() {
        hasConfig = new MockHasConfig();
    }

    @Test
    public void test_not_found_config_file_should_get_default_value() {
        final MockConfig config = hasConfig.computeConfig(log, new JsonObject());
        Assertions.assertNotNull(config);
    }

    @Test
    public void test_invalid_config_should_throw_exception() {
        Assertions.assertThrows(QWEException.class,
                                () -> hasConfig.computeConfig(log, new JsonObject().put("aaa", "yyy")));
    }

    static class MockHasConfig implements HasConfig<MockConfig> {

        @Override
        public @NonNull Class<MockConfig> configClass() {
            return MockConfig.class;
        }

        @Override
        public @NonNull String configFile() {
            return "notfound.json";
        }

    }

}

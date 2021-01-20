package io.github.zero88.msa.bp.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.msa.bp.exceptions.CarlException;
import io.github.zero88.msa.bp.utils.mock.MockConfig;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public class HasConfigTest {

    private MockHasConfig hasConfig;

    @BeforeEach
    public void setUp() {
        hasConfig = new MockHasConfig();
    }

    @Test
    public void test_not_found_config_file_should_get_default_value() {
        final MockConfig config = hasConfig.computeConfig(new JsonObject());
        Assertions.assertNotNull(config);
    }

    @Test
    public void test_invalid_config_should_throw_exception() {
        Assertions.assertThrows(CarlException.class,
                                () -> hasConfig.computeConfig(new JsonObject().put("aaa", "yyy")));
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

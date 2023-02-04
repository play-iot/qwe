package cloud.playio.qwe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.mock.MockPluginConfig;

import lombok.NonNull;

public class HasConfigTest {

    private MockHasConfig hasConfig;

    @BeforeEach
    public void setUp() {
        hasConfig = new MockHasConfig();
    }

    @Test
    public void test_not_found_config_file_should_get_default_value() {
        final MockPluginConfig config = hasConfig.computeConfig(new JsonObject());
        Assertions.assertNotNull(config);
    }

    @Test
    public void test_invalid_config_should_throw_exception() {
        Assertions.assertThrows(QWEException.class, () -> hasConfig.computeConfig(new JsonObject().put("aaa", "yyy")));
    }

    static class MockHasConfig implements HasConfig<MockPluginConfig> {

        @Override
        public @NonNull Class<MockPluginConfig> configClass() {
            return MockPluginConfig.class;
        }

        @Override
        public @NonNull String configFile() {
            return "notfound.json";
        }

    }

}

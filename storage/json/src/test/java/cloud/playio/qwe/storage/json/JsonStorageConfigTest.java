package cloud.playio.qwe.storage.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.IConfig;
import cloud.playio.qwe.JsonHelper;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.exceptions.ConfigException;
import cloud.playio.qwe.file.FileOption;

class JsonStorageConfigTest {

    @Test
    void test_default() {
        final JsonStorageConfig config = IConfig.fromClasspath("storage.json", JsonStorageConfig.class);
        JsonHelper.assertJson(JsonStorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void test_under_app() {
        final JsonStorageConfig config = IConfig.fromClasspath("app-cfg.json", JsonStorageConfig.class);
        JsonHelper.assertJson(JsonStorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void test_under_invalid_cfg() {
        TestHelper.assertCause(() -> IConfig.fromClasspath("invalid-cfg.json", JsonStorageConfig.class),
                               ConfigException.class, IllegalArgumentException.class, "Invalid configuration format");
    }

    @Test
    void serialize() {
        final JsonStorageConfig config = JsonStorageConfig.create();
        Assertions.assertTrue(config.getOption().isAutoCreate());
        final JsonObject json = config.toJson();
        System.out.println(json);
        JsonHelper.assertJson(JsonStorageConfig.builder().pluginDir("storage").build().toJson(), json);
    }

    @Test
    void deserialize() {
        JsonStorageConfig cfg = JsonStorageConfig.builder()
                                                 .pluginDir("abc")
                                                 .option(FileOption.builder()
                                                                   .autoCreate(false)
                                                                   .filePerms("rwxrwxr--")
                                                                   .build())
                                                 .build();
        Assertions.assertFalse(cfg.getOption().isAutoCreate());
        JsonObject json = cfg.toJson();
        System.out.println(json);
        JsonHelper.assertJson(json, IConfig.from(json, JsonStorageConfig.class).toJson());
    }

}

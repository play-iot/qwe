package io.zero88.qwe.storage.json;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.exceptions.ConfigException;
import io.zero88.qwe.file.FileOption;

class StorageConfigTest {

    @Test
    void test_default() {
        final StorageConfig config = IConfig.fromClasspath("storage.json", StorageConfig.class);
        JsonHelper.assertJson(StorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void test_under_app() {
        final StorageConfig config = IConfig.fromClasspath("app-cfg.json", StorageConfig.class);
        JsonHelper.assertJson(StorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void test_under_invalid_cfg() {
        TestHelper.assertCause(() -> IConfig.fromClasspath("invalid-cfg.json", StorageConfig.class),
                               ConfigException.class, IllegalArgumentException.class, "Invalid configuration format",
                               "Unrecognized field \"__app1__\" (class io.zero88.qwe.storage.json" +
                               ".StorageConfig$Builder), not marked as ignorable (7 known properties: " +
                               "\"serviceHandlerClass\", \"fullPath\", \"option\", \"subDir\", \"maxSizeInMB\", " +
                               "\"chunk\", \"serviceAddress\"])\n" +
                               " at [Source: UNKNOWN; line: -1, column: -1] (through reference chain: io.zero88.qwe" +
                               ".storage.json.StorageConfig$Builder[\"__app1__\"])");
    }

    @Test
    void serialize() {
        final StorageConfig config = StorageConfig.create();
        Assertions.assertTrue(config.getOption().isAutoCreate());
        final JsonObject json = config.toJson();
        System.out.println(json);
        JsonHelper.assertJson(StorageConfig.builder().subDir("storage").build().toJson(), json);
    }

    @Test
    void deserialize() {
        StorageConfig cfg = StorageConfig.builder()
                                         .subDir("abc")
                                         .option(FileOption.builder().autoCreate(false).filePerms("rwxrwxr--").build())
                                         .build();
        Assertions.assertFalse(cfg.getOption().isAutoCreate());
        JsonObject json = cfg.toJson();
        System.out.println(json);
        JsonHelper.assertJson(json, IConfig.from(json, StorageConfig.class).toJson());
    }

    @Test
    void getFullPath(@TempDir Path tmp) {
        final Path fullPath = StorageConfig.create().makeFullPath(tmp.toString()).fullPath();
        System.out.println(fullPath);
        Assertions.assertEquals(tmp.resolve("storage"), fullPath);
    }

}

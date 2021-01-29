package io.github.zero88.storage.json;

import java.nio.file.Path;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.JsonHelper;
import io.github.zero88.qwe.file.FileOption;
import io.vertx.core.json.JsonObject;

class StorageConfigTest {

    @Test
    void test_default() throws JSONException {
        final StorageConfig config = IConfig.fromClasspath("storage.json", StorageConfig.class);
        JsonHelper.assertJson(StorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void test_under_app() throws JSONException {
        final StorageConfig config = IConfig.fromClasspath("app-cfg.json", StorageConfig.class);
        JsonHelper.assertJson(StorageConfig.create().toJson(), config.toJson());
    }

    @Test
    void serialize() throws JSONException {
        final StorageConfig config = StorageConfig.create();
        Assertions.assertTrue(config.getOption().isAutoCreate());
        final JsonObject json = config.toJson();
        System.out.println(json);
        JsonHelper.assertJson(StorageConfig.builder().subDir("storage").build().toJson(), json);
    }

    @Test
    void deserialize() throws JSONException {
        StorageConfig config = StorageConfig.builder()
                                            .subDir("abc")
                                            .option(
                                                FileOption.builder().autoCreate(false).filePerms("rwxrwxr--").build())
                                            .build();
        Assertions.assertFalse(config.getOption().isAutoCreate());
        JsonObject json = config.toJson();
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

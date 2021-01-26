package io.github.zero88.storage.json;

import io.github.zero88.qwe.file.ReadableFile;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public class JsonStorageLoader {

    public Single<JsonArray> loadArray(@NonNull Vertx vertx, @NonNull StorageConfig config, @NonNull String fileName) {
        return helper(vertx, config).loadArray(config.fullPath().resolve(fileName), config.getOption());
    }

    public Single<JsonObject> loadJson(@NonNull Vertx vertx, @NonNull StorageConfig config, @NonNull String fileName) {
        return helper(vertx, config).loadJson(config.fullPath().resolve(fileName), config.getOption());
    }

    private ReadableFile helper(@NonNull Vertx vertx, @NonNull StorageConfig config) {
        return ReadableFile.builder().vertx(vertx).maxSize(config.getMaxSizeInMB()).build();
    }

}

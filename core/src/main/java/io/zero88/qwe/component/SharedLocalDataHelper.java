package io.zero88.qwe.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;

import lombok.NonNull;

/**
 * SharedData delegate
 */
@SuppressWarnings("unchecked")
public interface SharedLocalDataHelper {

    Logger LOGGER = LoggerFactory.getLogger(SharedLocalDataHelper.class);

    static <D> D getLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey) {
        LOGGER.trace("GET | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        return (D) Functions.getIfThrow(
            () -> vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).get(dataKey),
            e -> LOGGER.warn("Data value Type is not matching with expected data key {}", dataKey, e)).orElse(null);
    }

    static <D> D addLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey, D data) {
        LOGGER.trace("ADD | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).put(Strings.requireNotBlank(dataKey), data);
        return data;
    }

    static <D> D removeLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey) {
        LOGGER.trace("POP | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        return (D) Functions.getIfThrow(
            () -> vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).get(dataKey),
            e -> LOGGER.warn("Data value Type is not matching with expected data key {}", dataKey, e)).orElse(null);
    }

}

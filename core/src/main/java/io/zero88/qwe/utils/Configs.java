package io.zero88.qwe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Reflections;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.ErrorCode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configs {

    public static JsonObject loadJsonConfig(String file) {
        return Optional.ofNullable(Reflections.contextClassLoader().getResourceAsStream(file))
                       .map(Configs::readAsJson)
                       .orElseGet(() -> {
                           log.warn("Resource file '" + file + "' not found");
                           return new JsonObject();
                       });
    }

    public static JsonObject readAsJson(@NonNull InputStream resourceAsStream) {
        try (Scanner scanner = new Scanner(resourceAsStream).useDelimiter("\\A")) {
            return new JsonObject(scanner.next());
        } catch (DecodeException | NoSuchElementException e) {
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Config file is not valid JSON object", e);
        }
    }

    public static JsonArray readAsArray(@NonNull InputStream resourceAsStream) {
        try (Scanner scanner = new Scanner(resourceAsStream).useDelimiter("\\A")) {
            return new JsonArray(scanner.next());
        } catch (DecodeException | NoSuchElementException e) {
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Config file is not valid JSON object", e);
        }
    }

    public static Properties loadPropertiesConfig(String file) {
        Properties properties = new Properties();
        final InputStream resourceAsStream = Reflections.contextClassLoader().getResourceAsStream(file);
        if (Objects.isNull(resourceAsStream)) {
            log.warn("File not found");
            return properties;
        }
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            log.warn("Cannot load to properties file", e);
        } finally {
            FileUtils.silentClose(resourceAsStream);
        }
        return properties;
    }

}

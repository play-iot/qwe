package io.github.zero88.qwe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Reflections;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configs {

    public static JsonObject loadJsonConfig(String file) {
        final InputStream resourceAsStream = Reflections.contextClassLoader().getResourceAsStream(file);
        if (Objects.isNull(resourceAsStream)) {
            log.warn("File not found");
            return new JsonObject();
        }
        try (Scanner scanner = new Scanner(resourceAsStream).useDelimiter("\\A")) {
            return new JsonObject(scanner.next());
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

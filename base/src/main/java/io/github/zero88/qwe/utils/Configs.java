package io.github.zero88.qwe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Reflections;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configs {

    private static final Logger logger = LoggerFactory.getLogger(Configs.class);

    public static JsonObject loadJsonConfig(String file) {
        final InputStream resourceAsStream = Reflections.contextClassLoader().getResourceAsStream(file);
        if (Objects.isNull(resourceAsStream)) {
            logger.warn("File not found");
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
            logger.warn("File not found");
            return properties;
        }
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            logger.warn("Cannot load to properties file", e);
        } finally {
            FileUtils.silentClose(resourceAsStream);
        }
        return properties;
    }

}

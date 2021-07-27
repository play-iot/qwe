package io.zero88.qwe.utils;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.zero88.repl.Reflections;
import io.github.zero88.utils.DateTimes;
import io.github.zero88.utils.DateTimes.Iso8601Formatter;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.QWEException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    public static JsonObject putIfNotNull(@NonNull JsonObject json, @NonNull String name, Object value) {
        return putIfNotNull(json, Collections.singletonMap(name, value));
    }

    public static JsonObject putIfNotNull(@NonNull JsonObject json, @NonNull Map<String, Object> keyValues) {
        keyValues.entrySet()
                 .stream()
                 .filter(entry -> Objects.nonNull(entry.getValue()))
                 .forEach(entry -> json.put(entry.getKey(), entry.getValue()));
        return json;
    }

    public static JsonObject putIfNotNull(@NonNull JsonObject json, @NonNull JsonObject keyValues) {
        keyValues.stream()
                 .filter(entry -> Objects.nonNull(entry.getValue()))
                 .forEach(entry -> json.put(entry.getKey(), entry.getValue()));
        return json;
    }

    public static String kvMsg(@NonNull Object key, @NonNull Object value) {
        return key + "=" + value;
    }

    public static String kvMsg(@NonNull JsonObject json) {
        return json.stream()
                   .filter(entry -> Objects.nonNull(entry.getValue()))
                   .map(kvMsg())
                   .collect(Collectors.joining(" and "));
    }

    @NonNull
    public static Function<Entry, String> kvMsg() {
        return entry -> kvMsg(entry.getKey(), entry.getValue());
    }

    public static JsonObject formatDate(@NonNull Date date) {
        return formatDate(date, null);
    }

    public static JsonObject formatDate(@NonNull Date date, TimeZone timeZone) {
        final ZoneId zoneId = Objects.isNull(timeZone) ? ZoneId.systemDefault() : timeZone.toZoneId();
        final ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
        final ZonedDateTime utcTime = DateTimes.toUTC(zonedDateTime);
        return new JsonObject().put("local", Iso8601Formatter.format(zonedDateTime))
                               .put("utc", Iso8601Formatter.format(utcTime));
    }

    public static JsonObject loadJsonInClasspath(String file) {
        return loadJsonInCp(file, true);
    }

    public static JsonObject silentLoadJsonInClasspath(String file) {
        return loadJsonInCp(file, false);
    }

    public static JsonObject readAsJson(@NonNull InputStream resourceAsStream) {
        try (Scanner scanner = new Scanner(resourceAsStream).useDelimiter("\\A")) {
            return new JsonObject(scanner.next());
        } catch (DecodeException | NoSuchElementException e) {
            throw new QWEException(ErrorCode.INVALID_ARGUMENT, "Config file is not valid JSON object", e);
        }
    }

    public static JsonArray readAsArray(@NonNull InputStream resourceAsStream) {
        try (Scanner scanner = new Scanner(resourceAsStream).useDelimiter("\\A")) {
            return new JsonArray(scanner.next());
        } catch (DecodeException | NoSuchElementException e) {
            throw new QWEException(ErrorCode.INVALID_ARGUMENT, "Config file is not valid JSON object", e);
        }
    }

    public static Optional<String> findString(JsonObject filter, String attribute) {
        return Optional.ofNullable(filter).flatMap(f -> Optional.ofNullable(f.getString(attribute)));
    }

    private static JsonObject loadJsonInCp(String file, boolean logIt) {
        return Optional.ofNullable(Reflections.contextClassLoader().getResourceAsStream(file))
                       .map(JsonUtils::readAsJson)
                       .orElseGet(() -> {
                           if (logIt) {
                               log.warn("Resource file '" + file + "' not found");
                           }
                           return new JsonObject();
                       });
    }

}

package io.github.zero88.msa.bp.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.zero88.utils.DateTimes;
import io.github.zero88.utils.DateTimes.Iso8601Formatter;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

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

}

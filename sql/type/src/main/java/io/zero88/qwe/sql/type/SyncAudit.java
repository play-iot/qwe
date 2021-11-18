package io.zero88.qwe.sql.type;

import java.time.OffsetDateTime;

import io.github.zero88.utils.DateTimes;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.sql.Status;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public final class SyncAudit implements JsonData {

    private OffsetDateTime lastSuccessTime;
    private JsonObject lastSuccessMessage;
    private OffsetDateTime syncedTime;
    private Status status;
    private JsonObject data;
    private String by;

    public static SyncAudit unknown() {
        return new SyncAudit(null, null, null, Status.UNDEFINED,
                             new JsonObject().put("error", "Unknown sync audit information"), null);
    }

    public static SyncAudit notYetSynced(String message) {
        return new SyncAudit(null, null, null, Status.INITIAL,
                             new JsonObject().put("message", Strings.fallback(message, "Not yet synced")), null);
    }

    public static SyncAudit notYetSynced(@NonNull SyncAudit prevSync, String message) {
        OffsetDateTime lastSuccess = Status.SUCCESS == prevSync.status ? prevSync.syncedTime : prevSync.lastSuccessTime;
        JsonObject lastSuccessMsg = Status.SUCCESS == prevSync.status ? prevSync.data : prevSync.lastSuccessMessage;
        return new SyncAudit(lastSuccess, lastSuccessMsg, null, Status.INITIAL,
                             new JsonObject().put("message", Strings.fallback(message, "Not yet synced")), null);
    }

    public static SyncAudit success(@NonNull SyncAudit prevSync, @NonNull JsonObject response, @NonNull String by) {
        OffsetDateTime lastSuccess = Status.SUCCESS == prevSync.status ? prevSync.syncedTime : prevSync.lastSuccessTime;
        JsonObject lastSuccessMsg = Status.SUCCESS == prevSync.status ? prevSync.data : prevSync.lastSuccessMessage;
        return new SyncAudit(lastSuccess, lastSuccessMsg, DateTimes.now(), Status.SUCCESS, response, by);
    }

    public static SyncAudit error(@NonNull SyncAudit prevSync, @NonNull JsonObject error, @NonNull String by) {
        OffsetDateTime lastSuccess = Status.SUCCESS == prevSync.status ? prevSync.syncedTime : prevSync.lastSuccessTime;
        JsonObject lastSuccessMsg = Status.SUCCESS == prevSync.status ? prevSync.data : prevSync.lastSuccessMessage;
        return new SyncAudit(lastSuccess, lastSuccessMsg, DateTimes.now(), Status.FAILED, error, by);
    }

}

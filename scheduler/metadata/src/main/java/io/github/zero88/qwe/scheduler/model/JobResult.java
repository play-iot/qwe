package io.github.zero88.qwe.scheduler.model;

import java.util.Date;

import io.github.zero88.qwe.dto.ErrorMessage;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.event.Status;
import io.vertx.core.json.JsonObject;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public final class JobResult implements JsonData {

    private final Status status;
    private final String jobKey;
    private final String triggerKey;
    private final Date fireTime;
    private final String fireId;
    private final JsonObject result;
    private final ErrorMessage error;

}

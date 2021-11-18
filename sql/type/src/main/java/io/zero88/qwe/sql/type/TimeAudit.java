package io.zero88.qwe.sql.type;

import java.time.OffsetDateTime;
import java.util.Objects;

import io.github.zero88.utils.DateTimes;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public final class TimeAudit implements JsonData {

    private OffsetDateTime createdTime;
    private String createdBy;
    private OffsetDateTime lastModifiedTime;
    private String lastModifiedBy;
    private int revision;

    public static TimeAudit created(String createdBy) {
        return new TimeAudit(DateTimes.now(), createdBy, null, null, 1);
    }

    public static TimeAudit modified(TimeAudit timeAudit, String lastModifiedBy) {
        if (Objects.isNull(timeAudit)) {
            return new TimeAudit(null, null, DateTimes.now(), lastModifiedBy, 1);
        }
        timeAudit.setRevision(timeAudit.getRevision() + 1);
        timeAudit.setLastModifiedBy(lastModifiedBy);
        timeAudit.setLastModifiedTime(DateTimes.now());
        return timeAudit;
    }

}

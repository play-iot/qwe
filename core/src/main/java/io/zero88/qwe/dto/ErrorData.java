package io.zero88.qwe.dto;

import java.util.Objects;

import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = ErrorData.Builder.class)
public final class ErrorData implements JsonData {

    @NonNull
    private final ErrorMessage error;
    private final JsonObject extraInfo;


    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        @Setter
        @Accessors(fluent = true)
        private Throwable throwable;

        public ErrorData build() {
            final ErrorMessage err = Objects.nonNull(throwable) ? ErrorMessage.parse(throwable) : error;
            return new ErrorData(err, extraInfo);
        }

    }

}

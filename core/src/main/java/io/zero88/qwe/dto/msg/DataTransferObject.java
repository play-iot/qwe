package io.zero88.qwe.dto.msg;

import java.io.Serializable;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface DataTransferObject extends Serializable, JsonData {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class StandardKey {

        public static final String FILTER = "filter";
        public static final String HEADERS = "headers";
        public static final String BODY = "body";
        public static final String PAGINATION = "pagination";
        public static final String SORT = "sort";

    }

    @JsonProperty(value = StandardKey.BODY)
    JsonObject body();

    @JsonProperty(value = StandardKey.HEADERS)
    JsonObject headers();

}

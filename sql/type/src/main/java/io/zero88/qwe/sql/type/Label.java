package io.zero88.qwe.sql.type;

import io.zero88.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@ToString
public final class Label implements JsonData {

    private final String label;
    private final String description;

}

package cloud.playio.qwe.sql.type;

import cloud.playio.qwe.dto.JsonData;

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

package io.zero88.qwe.micro.transfomer;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Status;
import io.zero88.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

@Getter
@FieldNameConstants
@Jacksonized
@Builder(builderClassName = "Builder")
public final class RecordOutput implements JsonData {

    private final String registration;
    private final String name;
    private final String type;
    private final Status status;
    private final String endpoint;
    private final JsonArray paths;
    private final JsonObject location;
    private final JsonObject metadata;

}

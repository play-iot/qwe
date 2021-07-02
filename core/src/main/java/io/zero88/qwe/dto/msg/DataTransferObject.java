package io.zero88.qwe.dto.msg;

import java.io.Serializable;
import java.util.Objects;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DataTransferObject extends Serializable, JsonData {

    @JsonProperty(value = "body")
    JsonObject body();

    @JsonProperty(value = "headers")
    JsonObject headers();

    @SuppressWarnings("unchecked")
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    abstract class AbstractDTO implements DataTransferObject {

        private JsonObject headers = new JsonObject();
        private JsonObject body = new JsonObject();

        AbstractDTO(JsonObject headers, JsonObject body) {
            this.headers = Objects.nonNull(headers) ? headers : new JsonObject();
            this.body = body;
        }

        @Override
        public final JsonObject body() { return body; }

        @Override
        public final JsonObject headers() { return headers; }

        public <T extends AbstractDTO> T setBody(JsonObject body) {
            this.body = body;
            return (T) this;
        }

        public <T extends AbstractDTO> T setHeaders(JsonObject headers) {
            this.headers = headers;
            return (T) this;
        }

    }

}

package io.zero88.qwe.dto.msg;

import java.util.Objects;

import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings({"unchecked", "rawtypes"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class AbstractDTO<T extends AbstractDTO> implements DataTransferObject {

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

    public T setBody(JsonObject body) {
        this.body = body;
        return (T) this;
    }

    public T setHeaders(JsonObject headers) {
        this.headers = headers;
        return (T) this;
    }

}

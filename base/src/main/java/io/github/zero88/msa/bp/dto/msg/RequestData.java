package io.github.zero88.msa.bp.dto.msg;

import java.util.Objects;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.dto.jpa.Pagination;
import io.github.zero88.msa.bp.dto.jpa.Sort;
import io.github.zero88.msa.bp.dto.msg.DataTransferObject.AbstractDTO;
import io.github.zero88.msa.bp.event.EventMessage;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public final class RequestData extends AbstractDTO {

    @NonNull
    @JsonProperty(value = "filter")
    private RequestFilter filter;
    @JsonProperty(value = "pagination")
    private Pagination pagination;
    @JsonProperty(value = "sort")
    private Sort sort;

    @JsonCreator
    private RequestData(@JsonProperty(value = "headers") JsonObject headers,
                        @JsonProperty(value = "body") JsonObject body,
                        @JsonProperty(value = "filter") JsonObject filter,
                        @JsonProperty(value = "pagination") Pagination pagination,
                        @JsonProperty(value = "sort") Sort sort) {
        super(headers, body);
        this.filter = new RequestFilter(Objects.nonNull(filter) ? filter : new JsonObject());
        this.pagination = pagination;
        this.sort = sort;
    }

    public static Builder builder() { return new Builder(); }

    public static RequestData from(@NonNull EventMessage msg) {
        return builder().body(msg.getData()).build();
    }

    @Override
    public JsonObject toJson() {
        if (Objects.isNull(sort) || sort.isEmpty()) {
            return JsonData.MAPPER.convertValue(this, JsonObject.class);
        }
        return JsonData.MAPPER.convertValue(this, JsonObject.class).put("sort", sort.toJson());
    }

    public static class Builder {

        private JsonObject headers;
        private JsonObject body;
        private JsonObject filter;
        private Pagination pagination;
        private Sort sort;

        public Builder headers(JsonObject headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(JsonObject body) {
            this.body = body;
            return this;
        }

        public Builder filter(JsonObject filter) {
            this.filter = filter;
            return this;
        }

        public Builder pagination(Pagination pagination) {
            this.pagination = pagination;
            return this;
        }

        public Builder sort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public RequestData build() {
            return new RequestData(headers, body, filter, pagination, sort);
        }

    }

}

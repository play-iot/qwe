package cloud.playio.qwe.dto.msg;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.jpa.Pagination;
import cloud.playio.qwe.dto.jpa.Sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public final class RequestData extends AbstractDTO<RequestData> {

    /**
     * @see RequestFilter
     */
    @NonNull
    @JsonProperty(value = StandardKey.FILTER)
    private RequestFilter filter;
    @JsonProperty(value = StandardKey.PAGINATION)
    private Pagination pagination;
    @JsonProperty(value = StandardKey.SORT)
    private Sort sort;

    @JsonCreator
    private RequestData(@JsonProperty(value = StandardKey.HEADERS) JsonObject headers,
                        @JsonProperty(value = StandardKey.BODY) JsonObject body,
                        @JsonProperty(value = StandardKey.FILTER) JsonObject filter,
                        @JsonProperty(value = StandardKey.PAGINATION) Pagination pagination,
                        @JsonProperty(value = StandardKey.SORT) Sort sort) {
        super(headers, body);
        this.filter = new RequestFilter(Objects.nonNull(filter) ? filter : new JsonObject());
        this.pagination = pagination;
        this.sort = sort;
    }

    public static Builder builder() { return new Builder(); }

    public static RequestData empty() {
        return builder().build();
    }

    @Override
    public JsonObject toJson() {
        if (Objects.isNull(sort) || sort.isEmpty()) {
            return JsonData.MAPPER.convertValue(this, JsonObject.class);
        }
        return JsonData.MAPPER.convertValue(this, JsonObject.class).put("sort", sort.toJson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers(), body(), filter, pagination, sort);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RequestData)) {
            return false;
        }
        return toJson().equals(((RequestData) obj).toJson());
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

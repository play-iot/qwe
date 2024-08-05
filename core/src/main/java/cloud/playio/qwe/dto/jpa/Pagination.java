package cloud.playio.qwe.dto.jpa;

import io.github.zero88.utils.Strings;
import io.zero88.jpa.Pageable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(value="perPage")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pagination implements Pageable, JsonData {

    @JsonProperty(RequestFilter.PAGE)
    private int page;
    @JsonProperty(RequestFilter.PAGE_SIZE)
    private int pageSize;

    @Override
    public int getPage() { return page; }

    @Override
    public int getPageSize() { return pageSize; }

    public static Pagination oneValue() {
        return Pagination.builder().pageSize(1).page(1).build();
    }

    public static Builder builder() { return new Builder(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private static final int DEFAULT_PAGE_SIZE = 20;
        private static final int DEFAULT_PAGE = 1;

        private int page = DEFAULT_PAGE;
        private int pageSize = DEFAULT_PAGE_SIZE;

        public Builder page(int page) {
            this.page = Math.max(DEFAULT_PAGE, page);
            return this;
        }

        public Builder page(String page) {
            this.page = Math.max(DEFAULT_PAGE, Strings.convertToInt(page, DEFAULT_PAGE));
            return this;
        }

        public Builder pageSize(int perPage) {
            this.pageSize = perPage > 0 ? Math.min(perPage, DEFAULT_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
            return this;
        }

        public Builder pageSize(String perPage) {
            final int pp = Strings.convertToInt(perPage, DEFAULT_PAGE_SIZE);
            this.pageSize = pp > 0 ? Math.min(pp, DEFAULT_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
            return this;
        }

        public Pagination build() {
            return new Pagination(page, pageSize);
        }

    }

}

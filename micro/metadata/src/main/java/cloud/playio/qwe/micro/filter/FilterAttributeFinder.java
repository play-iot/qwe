package cloud.playio.qwe.micro.filter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.utils.JsonUtils;

public interface FilterAttributeFinder<T> {

    @NotNull String attribute();

    /**
     * Find an attribute from given filter
     *
     * @param filter the filter
     * @return attribute
     */
    @Nullable T findAttribute(JsonObject filter);

    interface FilterStringFinder extends FilterAttributeFinder<String> {

        @Override
        default @Nullable String findAttribute(JsonObject filter) {
            return JsonUtils.findString(filter, attribute()).orElse(null);
        }

    }

}

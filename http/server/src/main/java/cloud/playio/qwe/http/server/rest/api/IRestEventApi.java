package cloud.playio.qwe.http.server.rest.api;

import java.util.List;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EventPattern;
import cloud.playio.qwe.http.EventHttpApi;
import cloud.playio.qwe.http.HttpUtils;
import cloud.playio.qwe.http.server.RouterConfig;
import cloud.playio.qwe.http.server.rest.handler.RestEventApiDispatcher;

/**
 * Represents for REST event API
 *
 * @param <C> type of {@link RouterConfig}
 * @see EventHttpApi
 * @since 1.0.0
 */
public interface IRestEventApi<C extends RouterConfig> extends EventHttpApi {

    IRestEventApi<C> setup(C config, SharedDataLocalProxy sharedData);

    default List<String> contentTypes() {
        return HttpUtils.JSON_CONTENT_TYPES;
    }

    default EventPattern pattern() {
        return EventPattern.REQUEST_RESPONSE;
    }

    default <T extends RestEventApiDispatcher> Class<T> dispatcher() {
        return null;
    }

}

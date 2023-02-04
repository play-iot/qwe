package cloud.playio.qwe.http.server.rest.api;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.http.server.config.ApiConfig;

/**
 * Represents for the resource API that is connected via {@code HTTP} protocol but its handler relies on {@code
 * EventBus}
 */
public interface RestEventApi extends IRestEventApi<ApiConfig> {

    @Override
    default RestEventApi setup(ApiConfig config, SharedDataLocalProxy sharedData) {
        return this;
    }

}

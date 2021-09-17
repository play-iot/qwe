package io.zero88.qwe.http.server.rest.api;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.config.ApiConfig;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents for the resource API that is connected via {@code HTTP} protocol but its handler relies on {@code
 * EventBus}
 */
public interface RestEventApi extends IRestEventApi<ApiConfig> {

    @Override
    RestEventApi setup(ApiConfig config, SharedDataLocalProxy sharedData);

    abstract class AbstractRestEventApi implements RestEventApi, ApisSystem {

        @Getter
        @Accessors(fluent = true)
        protected String address;
        @Getter
        protected EventMethodDefinition definition;

    }

}

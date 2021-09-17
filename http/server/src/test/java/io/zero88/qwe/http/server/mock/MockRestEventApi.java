package io.zero88.qwe.http.server.mock;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.rest.api.RestEventApi.AbstractRestEventApi;

public class MockRestEventApi extends AbstractRestEventApi {

    @Override
    public MockRestEventApi setup(ApiConfig config, SharedDataLocalProxy sharedData) {
        this.address = "http.server.test";
        this.definition = EventMethodDefinition.create("/test/events", "/:event_id", mapping());
        return this;
    }

}

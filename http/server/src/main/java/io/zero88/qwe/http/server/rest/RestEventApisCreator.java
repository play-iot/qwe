package io.zero88.qwe.http.server.rest;

import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.rest.ApisCreator.RestEventApisCreatorImpl;

/**
 * @see RouterCreator
 */
public class RestEventApisCreator extends RestEventApisCreatorImpl<ApiConfig> {

    @Override
    protected String subFunction() {
        return "EventApi";
    }

}

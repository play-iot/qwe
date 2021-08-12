package io.zero88.qwe.http.server.rest;

import java.util.function.Function;

import io.zero88.qwe.http.server.HttpRuntimeConfig;
import io.zero88.qwe.http.server.HttpServerConfig;
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

    @Override
    protected void register(HttpRuntimeConfig runtimeConfig) {
        register(runtimeConfig.getRestEventApiClasses());
    }

    @Override
    public Function<HttpServerConfig, ApiConfig> lookupConfig() {
        return HttpServerConfig::getApiConfig;
    }

}

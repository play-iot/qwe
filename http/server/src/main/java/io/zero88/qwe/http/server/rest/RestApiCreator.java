package io.zero88.qwe.http.server.rest;

import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.config.ApiConfig;
import io.zero88.qwe.http.server.rest.api.RestApi;

import lombok.NonNull;

public class RestApiCreator extends ApisCreator<RestApi, ApiConfig> {

    @Override
    public @NonNull Router subRouter(@NonNull ApiConfig config, @NonNull SharedDataLocalProxy sharedData) {
        Object[] classes = getApis().toArray(new Class[] {});
        //TODO register RestAPI
        return Router.router(sharedData.getVertx());
    }

    @Override
    protected String subFunction() {
        return "RestAPI";
    }

}

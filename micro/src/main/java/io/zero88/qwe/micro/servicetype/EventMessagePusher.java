package io.zero88.qwe.micro.servicetype;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.ResponseData;

public interface EventMessagePusher {

    /**
     * Push data via Event Bus then consume reply data
     *
     * @param path        HTTP path
     * @param httpMethod  HTTP Method
     * @param requestData Request Data
     * @return a future
     */
    Future<ResponseData> execute(String path, HttpMethod httpMethod, JsonObject requestData);

}

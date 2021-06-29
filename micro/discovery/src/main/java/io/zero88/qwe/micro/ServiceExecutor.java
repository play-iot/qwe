package io.zero88.qwe.micro;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.ResponseData;

public interface ServiceExecutor extends HasServiceType {

    @Nullable JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedDataLocalProxy);

    Future<ResponseData> execute(ServiceReference serviceReference, RequestData requestData);

}

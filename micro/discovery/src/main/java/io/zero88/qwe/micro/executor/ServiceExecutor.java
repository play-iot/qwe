package io.zero88.qwe.micro.executor;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.micro.HasServiceType;

public interface ServiceExecutor extends HasServiceType {

    @Nullable JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedData);

    Future<ResponseData> execute(SharedDataLocalProxy sharedData, ServiceReference serviceReference,
                                 RequestData requestData, RequestFilter filter);

}

package cloud.playio.qwe.micro.executor;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.micro.HasServiceType;

public interface ServiceExecutor extends HasServiceType {

    @Nullable JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedData);

    Future<ResponseData> execute(SharedDataLocalProxy sharedData, ServiceReference serviceReference,
                                 RequestData requestData, RequestFilter filter);

}

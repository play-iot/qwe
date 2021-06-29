package io.zero88.qwe.micro;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.dto.msg.ResponseData;

import lombok.NonNull;

public interface ServiceDiscoveryApi extends Supplier<ServiceDiscovery>, HasSharedData, HasLogger {

    Future<Record> register(@NonNull Record record);

    default CompositeFuture register(@NonNull Record... records) {
        return register(Arrays.asList(records));
    }

    CompositeFuture register(@NonNull List<Record> records);

    Future<Record> update(@NonNull Record record);

    Future<Record> updateMany(@NonNull RequestFilter filter, @NonNull JsonObject updateData);

    Future<Void> unregister(@NonNull RequestFilter filter);

    Future<@Nullable Record> findOne(@NonNull RequestFilter filter);

    Future<List<Record>> findMany(@NonNull RequestFilter filter);

    Future<ResponseData> execute(@NonNull RequestFilter filter, RequestData requestData);

}

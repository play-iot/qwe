package io.zero88.qwe.micro.filter;

import io.zero88.qwe.micro.servicetype.EventMessageService;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

import lombok.NonNull;

public interface ByPathPredicate {

    static boolean predicate(@NonNull Record record, @NonNull String path) {
        if (EventMessageService.TYPE.equals(record.getType())) {
            return new EventServicePathPredicate().test(record, path);
        }
        if (HttpEndpoint.TYPE.equals(record.getType())) {
            return new HttpEndpointPathPredicate().test(record, path);
        }
        return false;
    }

    boolean test(@NonNull Record record, @NonNull String path);

}

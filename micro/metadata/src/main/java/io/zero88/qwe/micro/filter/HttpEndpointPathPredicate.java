package io.zero88.qwe.micro.filter;

import io.github.zero88.utils.Urls;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpLocation;

import lombok.NonNull;

final class HttpEndpointPathPredicate implements ByPathPredicate {

    @Override
    public boolean test(@NonNull Record record, @NonNull String path) {
        HttpLocation location = new HttpLocation(record.getLocation());
        return location.getRoot().equals(Urls.combinePath(path));
    }

}

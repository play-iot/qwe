package io.zero88.qwe.micro.filter;

import io.github.zero88.utils.Urls;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;

public final class HttpEndpointPathPredicate implements ByPathPredicateFactory<HttpLocation> {

    @Override
    public String serviceType() {
        return HttpEndpoint.TYPE;
    }

    @Override
    public HttpLocation parseLocation(Record record) {
        return new HttpLocation(record.getLocation());
    }

    @Override
    public boolean test(HttpLocation location, String path, JsonObject filter) {
        return location.getRoot().equals(Urls.combinePath(path));
    }

}

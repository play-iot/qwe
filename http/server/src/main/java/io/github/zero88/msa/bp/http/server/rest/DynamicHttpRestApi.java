package io.github.zero88.msa.bp.http.server.rest;

import java.util.Optional;
import java.util.Set;

import io.github.zero88.msa.bp.http.server.ApiConstants;
import io.github.zero88.msa.bp.utils.Networks;
import io.github.zero88.utils.Urls;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;

import lombok.NonNull;

public interface DynamicHttpRestApi extends DynamicRestApi {

    static DynamicHttpRestApi create(@NonNull Record record) {
        final HttpLocation location = record.getLocation().mapTo(HttpLocation.class);
        final int order = Networks.priorityOrder(location.getRoot().length());
        final String path = Urls.combinePath(location.getRoot(), ApiConstants.WILDCARDS_ANY_PATH);
        return new DynamicHttpRestApi() {
            @Override
            public String path() { return path; }

            @Override
            public int order() { return order; }

            @Override
            public boolean useRequestData() { return true; }

            @Override
            public @NonNull String name() { return record.getName(); }

            @Override
            public JsonObject byMetadata() { return record.getMetadata(); }
        };
    }

    @Override
    default Optional<Set<String>> alternativePaths() { return Optional.empty(); }

    @Override
    default String type() { return HttpEndpoint.TYPE; }

}

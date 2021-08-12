package io.zero88.qwe.http.server.rest.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.micro.httpevent.ActionMethodMapping;
import io.zero88.qwe.micro.httpevent.EventMethodDefinition;
import io.zero88.qwe.micro.httpevent.RestEventApiMetadata;

import lombok.NonNull;

public abstract class AbstractRestEventApi implements RestEventApi, ApisSystem, HasLogger {

    private final ActionMethodMapping mapping;
    private final SortedMap<String, RestEventApiMetadata> restMetadata = new TreeMap<>(
        Comparator.comparingInt(String::length));

    protected AbstractRestEventApi() {
        this.mapping = initHttpEventMap();
    }

    protected ActionMethodMapping initHttpEventMap() {
        return CRUD_MAP;
    }

    protected void addRouter(String address, String apiPath, String paramPath) {
        addRouter(address, EventMethodDefinition.create(apiPath, paramPath, this));
    }

    protected void addRouter(@NonNull String address, @NonNull EventMethodDefinition definition) {
        if (restMetadata.containsKey(definition.getServicePath())) {
            logger().warn(decor("HTTP path [{}] is already registered, but might be bind to Event address [{}]"),
                          definition.getServicePath(), restMetadata.get(definition.getServicePath()).getAddress());
        }
        RestEventApiMetadata metadata = RestEventApiMetadata.builder()
                                                            .address(address)
                                                            .pattern(EventPattern.REQUEST_RESPONSE)
                                                            .definition(definition)
                                                            .build();
        restMetadata.putIfAbsent(definition.getServicePath(), metadata);
    }

    @Override
    public Collection<RestEventApiMetadata> getRestMetadata() {
        return Collections.unmodifiableCollection(restMetadata.values());
    }

    @Override
    public Map<EventAction, HttpMethod> get() {return mapping.get();}

}

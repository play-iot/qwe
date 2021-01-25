package io.github.zero88.qwe.http.server.rest;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.http.event.RestEventApiMetadata;
import io.github.zero88.qwe.micro.http.ActionMethodMapping;
import io.github.zero88.qwe.micro.http.EventMethodDefinition;
import io.vertx.core.http.HttpMethod;

import lombok.NonNull;

public abstract class AbstractRestEventApi implements RestEventApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ActionMethodMapping mapping;
    private final SortedMap<String, RestEventApiMetadata> restMetadata = new TreeMap<>(
        Comparator.comparingInt(String::length));
    protected SharedDataLocalProxy proxy;

    protected AbstractRestEventApi() {
        this.mapping = initHttpEventMap();
    }

    protected ActionMethodMapping initHttpEventMap() {
        return ActionMethodMapping.CRUD_MAP;
    }

    protected void addRouter(@NonNull EventModel eventModel, String apiPath) {
        addRouter(eventModel, EventMethodDefinition.create(apiPath, this));
    }

    protected void addRouter(@NonNull EventModel eventModel, String apiPath, String paramPath) {
        addRouter(eventModel, EventMethodDefinition.create(apiPath, paramPath, this));
    }

    protected void addRouter(@NonNull EventModel eventModel, @NonNull EventMethodDefinition definition) {
        addRouter(eventModel.getAddress(), eventModel.getPattern(), definition);
    }

    protected void addRouter(@NonNull String address, @NonNull EventPattern pattern,
                             @NonNull EventMethodDefinition definition) {
        if (restMetadata.containsKey(definition.getServicePath())) {
            logger.warn("HTTP path '{}' is already registered, but might different Event address '{}'",
                        definition.getServicePath(), restMetadata.get(definition.getServicePath()).getAddress());
        }
        restMetadata.putIfAbsent(definition.getServicePath(), RestEventApiMetadata.builder()
                                                                                  .address(address)
                                                                                  .pattern(pattern)
                                                                                  .definition(definition)
                                                                                  .build());
    }

    @Override
    public Collection<RestEventApiMetadata> getRestMetadata() {
        return Collections.unmodifiableCollection(restMetadata.values());
    }

    @Override
    public AbstractRestEventApi registerProxy(SharedDataLocalProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Map<EventAction, HttpMethod> get() { return mapping.get(); }

}

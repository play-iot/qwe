package io.zero88.qwe.http.server.rest.api;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.EventMethodMapping;
import io.zero88.qwe.micro.servicetype.EventMessageHttpService;

import lombok.NonNull;

public interface DynamicRestEventApi extends DynamicRestApi {

    static DynamicRestEventApi create(Record record) {
        EventMethodDefinition definition = EventMethodDefinition.from(record.getLocation());
        Set<String> paths = Collections.unmodifiableSet(definition.getMapping()
                                                                  .stream()
                                                                  .map(EventMethodMapping::getCapturePath)
                                                                  .filter(Strings::isNotBlank)
                                                                  .collect(Collectors.toSet()));
        return new DynamicRestEventApi() {

            @Override
            public String path() { return definition.getRegexPath(); }

            @Override
            public int order() { return definition.getOrder(); }

            @Override
            public Optional<Set<String>> alternativePaths() {
                return paths.isEmpty() ? Optional.empty() : Optional.of(paths);
            }

            @Override
            public @NonNull String name() { return record.getName(); }

            @Override
            public JsonObject byMetadata() { return record.getMetadata(); }
        };
    }

    @Override
    default String serviceType() {
        return EventMessageHttpService.TYPE;
    }

}

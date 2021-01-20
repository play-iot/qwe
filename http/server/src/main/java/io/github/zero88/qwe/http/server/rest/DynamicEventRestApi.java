package io.github.zero88.qwe.http.server.rest;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;
import io.github.zero88.qwe.micro.metadata.EventMethodMapping;
import io.github.zero88.qwe.micro.type.EventMessageService;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

public interface DynamicEventRestApi extends DynamicRestApi {

    static DynamicEventRestApi create(Record record) {
        final EventMethodDefinition definition = JsonData.from(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG, new JsonObject()),
            EventMethodDefinition.class);
        Set<String> paths = Collections.unmodifiableSet(definition.getMapping()
                                                                  .stream()
                                                                  .filter(m -> Strings.isNotBlank(m.getCapturePath()))
                                                                  .map(EventMethodMapping::getCapturePath)
                                                                  .collect(Collectors.toSet()));
        return new DynamicEventRestApi() {

            @Override
            public String path() { return definition.getServicePath(); }

            @Override
            public int order() { return definition.getOrder(); }

            @Override
            public boolean useRequestData() { return definition.isUseRequestData(); }

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
    default String type() {
        return EventMessageService.TYPE;
    }

}

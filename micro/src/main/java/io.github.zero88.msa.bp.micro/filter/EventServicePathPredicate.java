package io.github.zero88.msa.bp.micro.filter;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.micro.metadata.EventMethodDefinition;
import io.github.zero88.msa.bp.micro.type.EventMessageService;
import io.github.zero88.utils.Urls;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;

final class EventServicePathPredicate implements ByPathPredicate {

    @Override
    public boolean test(@NonNull Record record, @NonNull String path) {
        EventMethodDefinition definition = JsonData.convert(
            record.getMetadata().getJsonObject(EventMessageService.EVENT_METHOD_CONFIG), EventMethodDefinition.class);
        return definition.getServicePath().equals(Urls.combinePath(path));
    }

}

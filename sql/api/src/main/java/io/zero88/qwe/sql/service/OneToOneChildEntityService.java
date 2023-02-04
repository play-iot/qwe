package cloud.playio.qwe.sql.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.marker.OneToOneEntityMarker;

import lombok.NonNull;

/**
 * Represents for an entity service that has {@code one-to-one} relationship to other entities and in business context,
 * it is as {@code child} entity.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @see ReferencingEntityService
 * @see OneToOneEntityMarker
 * @since 1.0.0
 */
public interface OneToOneChildEntityService<P extends JsonRecord, M extends EntityMetadata>
    extends ReferencingEntityService<P, M>, OneToOneEntityMarker {

    static Set<EventAction> availableEvents(@NonNull Collection<EventAction> availableEvents) {
        return availableEvents.stream().filter(action -> action != EventAction.GET_LIST).collect(Collectors.toSet());
    }

    @Override
    default @NonNull Collection<EventAction> getAvailableEvents() {
        return availableEvents(ReferencingEntityService.super.getAvailableEvents());
    }

}

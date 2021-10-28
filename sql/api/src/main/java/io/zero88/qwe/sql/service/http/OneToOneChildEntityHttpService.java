package io.zero88.qwe.sql.service.http;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.utils.Urls;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.http.httpevent.ActionMethodMapping;
import io.zero88.qwe.http.httpevent.EventHttpService;
import io.zero88.qwe.http.httpevent.EventMethodDefinition;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.service.OneToOneChildEntityService;

/**
 * Represents for an entity HTTP service that has {@code one-to-one} relationship to other entities and in business
 * context, it is as {@code child} entity.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @since 1.0.0
 */
public interface OneToOneChildEntityHttpService<P extends JsonRecord, M extends EntityMetadata>
    extends OneToOneChildEntityService<P, M>, EventHttpService {

    @Override
    default Set<EventMethodDefinition> definitions() {
        final ActionMethodMapping mapping = ActionMethodMapping.by(ActionMethodMapping.CRD_MAP, getAvailableEvents());
        final Function<EntityMetadata, EventMethodDefinition> function = meta -> EventMethodDefinition.create(
            Urls.combinePath(EntityHttpService.toCapturePath(meta), Urls.toPathWithLC(context().singularKeyName())),
            mapping);
        return Stream.of(dependantEntities().keys().stream().map(function).collect(Collectors.toSet()),
                         EntityHttpService.createDefinitions(mapping, context()))
                     .flatMap(Collection::stream)
                     .collect(Collectors.toSet());
    }

}

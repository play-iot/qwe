package io.zero88.qwe.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.UpdatableRecord;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.zero88.qwe.sql.validation.CompositeValidation;

import lombok.NonNull;

/**
 * Represents for Composite metadata.
 *
 * @param <K> Type of {@code primary key}
 * @param <P> Type of {@code JsonRecord}
 * @param <R> Type of {@code UpdatableRecord}
 * @param <C> Type of {@code CompositePojo}
 * @see EntityMetadata
 * @see CompositeValidation
 * @since 1.0.0
 */
public interface CompositeMetadata<K, P extends JsonRecord, R extends UpdatableRecord<R>, C extends CompositePojo<P, C>>
    extends EntityMetadata<K, P, R>, CompositeValidation<P, C> {

    @Override
    @SuppressWarnings("unchecked")
    @NonNull Class<C> modelClass();

    @Override
    default CompositeMetadata context() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull C parseFromEntity(@NonNull JsonObject entity) throws IllegalArgumentException {
        Map<String, JsonRecord> sub = subItems().stream()
                                                .filter(m -> entity.getValue(m.singularKeyName()) instanceof JsonObject)
                                                .collect(Collectors.toMap(EntityMetadata::singularKeyName,
                                                                          m -> m.parseFromEntity(entity.getJsonObject(
                                                                              m.singularKeyName()))));
        return ((C) ReflectionClass.createObject(modelClass()).fromJson(entity)).wrap(sub);
    }

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull C parseFromRequest(@NonNull JsonObject request) throws IllegalArgumentException {
        Map<String, JsonRecord> sub = subItems().stream()
                                                .filter(
                                                    m -> request.getValue(m.singularKeyName()) instanceof JsonObject)
                                                .collect(Collectors.toMap(EntityMetadata::singularKeyName,
                                                                          m -> m.parseFromRequest(request.getJsonObject(
                                                                              m.singularKeyName()))));
        return ((C) ReflectionClass.createObject(modelClass()).fromJson(request)).wrap(sub);
    }

    /**
     * Get raw class.
     *
     * @return the class
     * @since 1.0.0
     */
    @NonNull Class<P> rawClass();

    /**
     * Declares list of sub entity metadata.
     *
     * @return the list entity metadata
     * @since 1.0.0
     */
    @NonNull List<EntityMetadata> subItems();

    @Override
    @SuppressWarnings("unchecked")
    default C onCreating(RequestData reqData) throws IllegalArgumentException {
        return CompositeValidation.super.onCreating(reqData);
    }

    /**
     * Mapper record mapper.
     *
     * @param <REC> Type of {@code Record}
     * @return the record mapper
     * @since 1.0.0
     */
    default <REC extends Record> RecordMapper<REC, C> mapper() {
        return mapper(subItems().toArray(new EntityMetadata[] {}));
    }

    /**
     * Mapper record mapper.
     *
     * @param <REC>      Type of {@code Record}
     * @param references the references
     * @return the record mapper
     * @since 1.0.0
     */
    //TODO hack way due to jooq bug. Must guard the order of given references
    default <REC extends Record> RecordMapper<REC, C> mapper(EntityMetadata... references) {
        return r -> {
            final C into = r.into(table().fields()).into(modelClass());
            final Iterator<EntityMetadata> iterator = Stream.of(references).iterator();
            Map<String, List<Field>> group = Stream.of(
                r.fieldsRow().fields(IntStream.range(table().fields().length, r.fields().length).toArray()))
                                                   .collect(LinkedHashMap::new, (map, f) -> map.computeIfAbsent(
                                                       f.getQualifiedName().first(), k -> new ArrayList<>()).add(f),
                                                            Map::putAll);
            final Map<String, JsonRecord> other = new HashMap<>();
            for (List<Field> fields : group.values()) {
                if (!iterator.hasNext()) {
                    break;
                }
                final EntityMetadata ref = iterator.next();
                other.put(ref.singularKeyName(),
                          (JsonRecord) r.into(fields.toArray(new Field[0])).into(ref.modelClass()));
            }
            return (C) into.wrap(other);
        };
    }

    /**
     * Represents for Abstract composite metadata.
     *
     * @param <K> Type of {@code primary key}
     * @param <P> Type of {@code JsonRecord}
     * @param <R> Type of {@code UpdatableRecord}
     * @param <D> Type of {@code VertxDAO}
     * @param <C> Type of {@code CompositePojo}
     * @since 1.0.0
     */
    abstract class AbstractCompositeMetadata<K, P extends JsonRecord, R extends UpdatableRecord<R>,
                                                D extends JsonRecord<R, P, K>, C extends CompositePojo<P, C>>
        implements CompositeMetadata<K, P, R, C> {

        private final List<EntityMetadata> sub = new ArrayList<>();

        @Override
        public abstract @NonNull Class<C> modelClass();

        @Override
        public C parseFromEntity(@NonNull JsonObject entity) throws IllegalArgumentException {
            return CompositeMetadata.super.parseFromEntity(entity);
        }

        @Override
        public C parseFromRequest(@NonNull JsonObject request) throws IllegalArgumentException {
            return CompositeMetadata.super.parseFromRequest(request);
        }

        @Override
        public @NonNull List<EntityMetadata> subItems() {
            return sub;
        }

        @SuppressWarnings("unchecked")
        protected <T extends CompositeMetadata> T addSubItem(EntityMetadata... metadata) {
            sub.addAll(Stream.of(metadata).filter(Objects::nonNull).collect(Collectors.toList()));
            return (T) this;
        }

    }

}

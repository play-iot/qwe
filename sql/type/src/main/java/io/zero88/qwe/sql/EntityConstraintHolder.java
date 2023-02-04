package cloud.playio.qwe.sql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.ForeignKey;
import org.jooq.Key;
import org.jooq.Table;
import org.jooq.UniqueKey;

import io.github.zero88.repl.ReflectionField;

import lombok.NonNull;

/**
 * A {@code Holder} keeps information about {@code references/constraints} in database schema
 * <p>
 * In most case, it is decorator for EntityHandler
 *
 * @since 1.0.0
 */
public interface EntityConstraintHolder {

    EntityConstraintHolder BLANK = () -> null;

    /**
     * Declares {@code Key} class that modeling {@code foreign key} relationships and {@code constraints of tables} in
     * {@code schema}
     *
     * @return {@code Key} class
     * @since 1.0.0
     */
    Class keyClass();

    /**
     * Find list of table that link to given {@code table}.
     *
     * @param table the table
     * @return table references to given table
     * @since 1.0.0
     */
    default List<Table> referencesTo(@NonNull Table table) {
        return streamReferenceKeysTo(table).map(Key::getTable).collect(Collectors.toList());
    }

    /**
     * Find list of foreign key that reference to given {@code table}.
     *
     * @param table the table
     * @return foreign keys to given table
     * @see ForeignKey
     * @since 1.0.0
     */
    default List<ForeignKey> referenceKeysTo(@NonNull Table table) {
        return streamReferenceKeysTo(table).collect(Collectors.toList());
    }

    /**
     * Stream reference keys to given table.
     *
     * @param table the table
     * @return the stream of foreign key
     * @see ForeignKey
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default Stream<ForeignKey> streamReferenceKeysTo(@NonNull Table table) {
        return Optional.ofNullable(keyClass())
                       .map(kc -> ReflectionField.streamConstants(keyClass(), UniqueKey.class)
                                                 .filter(key -> key.getTable().equals(table))
                                                 .map(key -> (List<ForeignKey>) key.getReferences())
                                                 .flatMap(Collection::stream))
                       .orElse(Stream.empty());
    }

    /**
     * Reference to list.
     *
     * @param metadata the metadata
     * @return the list
     * @since 1.0.0
     */
    default List<ReferenceEntityMetadata> referenceTo(@NonNull EntityMetadata metadata) {
        return referenceTo(metadata.table());
    }

    default List<ReferenceEntityMetadata> referenceTo(@NonNull Table table) {
        return streamReferenceKeysTo(table).filter(fk -> fk.getKey().getTable().equals(table))
                                           .map(fk -> ReferenceEntityMetadata.builder().foreignKey(fk).build())
                                           .filter(ReferenceEntityMetadata::isValid)
                                           .collect(Collectors.toList());
    }

}

package io.zero88.qwe.sql;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.UUID64;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.tables.JsonTable;
import io.zero88.qwe.sql.type.TimeAudit;
import io.zero88.qwe.sql.validation.EntityValidation;

import lombok.NonNull;

/**
 * Entity metadata Holder
 *
 * @param <K> Type of {@code primary key}
 * @param <P> Type of {@code JsonRecord}
 * @param <R> Type of {@code UpdatableRecord}
 * @since 1.0.0
 */
public interface EntityMetadata<K, P extends JsonRecord, R extends UpdatableRecord<R>> extends EntityValidation<P> {

    /**
     * Create request key name.
     *
     * @param <POJO>      Type of {@code JsonRecord}
     * @param modelClass  the model class
     * @param jsonKeyName the json key name
     * @return request key name
     * @since 1.0.0
     */
    static <POJO extends JsonRecord> String createRequestKeyName(@NonNull Class<POJO> modelClass, String jsonKeyName) {
        return (Strings.toSnakeCaseLC(modelClass.getSimpleName()) + "_" +
                Strings.requireNotBlank(jsonKeyName)).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Create json key name.
     *
     * @param table the table
     * @return the json key name
     * @since 1.0.0
     */
    static String createJsonKeyName(@NonNull Table<? extends Record> table) {
        if (table.getPrimaryKey().getFields().size() != 1) {
            throw new UnsupportedOperationException("Doesn't support composite key or no primary key");
        }
        return table.getPrimaryKey().getFields().iterator().next().getName().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Defines enabling {@code time audit} in {@code application layer} instead of {@code database layer} by {@code DB
     * trigger}. It is helpful to add time audit in {@code create/update/patch} resource.
     *
     * @return {@code true} if enable time audit in application layer
     * @see TimeAudit
     * @since 1.0.0
     */
    default boolean enableTimeAudit() {
        return true;
    }

    /**
     * Declares entity table
     *
     * @return entity table
     * @see Table
     * @see JsonTable
     * @since 1.0.0
     */
    @NonNull JsonTable<R> table();

    /**
     * Declares Pojo model class
     *
     * @param <PP> Type of {@code JsonRecord}
     * @return model class
     * @since 1.0.0
     */
    @NonNull <PP extends P> Class<PP> modelClass();

    /**
     * Declares DAO class
     *
     * @return dao class
     * @since 1.0.0
     */
    @NonNull Class<Object> daoClass();

    @Override
    default EntityMetadata context() { return this; }

    /**
     * Defines request key name that represents for {@code primary/unique key} in {@code table} to lookup in doing
     * {@code get /update /patch /delete} resource
     *
     * @return request key name. Default is {@code <model_name>_<json_key_name>}
     * @apiNote It might not represents for actual primary key column in database table
     * @see #modelClass()
     * @since 1.0.0
     */
    @NonNull
    default String requestKeyName() {
        return createRequestKeyName(modelClass(), jsonKeyName());
    }

    /**
     * Primary key column is represented in {@code json} mode
     * <p>
     * Default is primary key column in lowercase
     *
     * @return primary key column in json
     * @apiNote It doesn't support composite key or no primary key in {@code table}
     * @see #table()
     * @since 1.0.0
     */
    @NonNull
    default String jsonKeyName() {
        return createJsonKeyName(table());
    }

    /**
     * Define response key name for single resource
     *
     * @return response key name
     * @apiNote Default is {@link #table()} name in lowercase
     * @since 1.0.0
     */
    default @NonNull String singularKeyName() {
        return table().getName().toLowerCase();
    }

    /**
     * Defines response key name for multiple resource
     *
     * @return response key name
     * @apiNote Default is {@link #singularKeyName()} appends "{@code s}" character
     * @since 1.0.0
     */
    default @NonNull String pluralKeyName() {
        return singularKeyName() + "s";
    }

    /**
     * Default order fields
     *
     * @return order fields
     * @see OrderField
     * @since 1.0.0
     */
    default @NonNull List<OrderField<?>> orderFields() {
        return Collections.singletonList((OrderField<?>) table().getField(jsonKeyName()).asc());
    }

    /**
     * Parses given data from entity database to {@code pojo} object
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param request Given entity data
     * @return {@code pojo} object resource
     * @throws IllegalArgumentException if cannot parse
     * @since 1.0.0
     */
    @NonNull
    @SuppressWarnings("unchecked")
    default <PP extends P> PP parseFromEntity(@NonNull JsonObject request) throws IllegalArgumentException {
        return (PP) ReflectionClass.createObject(modelClass()).fromJson(request);
    }

    /**
     * Parses given data from service request to {@code pojo} object
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param request Given request data
     * @return {@code pojo} object resource
     * @throws IllegalArgumentException if cannot parse
     * @since 1.0.0
     */
    @NonNull
    default <PP extends P> PP parseFromRequest(@NonNull JsonObject request) throws IllegalArgumentException {
        return parseFromEntity(request);
    }

    /**
     * Gets key.
     *
     * @param requestData the request data
     * @return the optional key
     * @throws IllegalArgumentException if cannot parse key in request data
     * @since 1.0.0
     */
    @NonNull
    default Optional<K> getKey(@NonNull RequestData requestData) throws IllegalArgumentException {
        return Optional.ofNullable(requestData.body())
                       .flatMap(body -> Optional.ofNullable(body.getValue(requestKeyName())))
                       .map(Object::toString)
                       .map(this::parseKey);
    }

    /**
     * Extract {@code primary/unique key} from request by {@link #requestKeyName()} then parse to proper data type
     *
     * @param requestData Request data
     * @return Actual primary key
     * @throws IllegalArgumentException if data key is invalid or missing
     * @see #requestKeyName()
     * @see #parseKey(String)
     * @since 1.0.0
     */
    @NonNull
    default K parseKey(@NonNull RequestData requestData) throws IllegalArgumentException {
        return getKey(requestData).orElseThrow(() -> new IllegalArgumentException("Missing key " + requestKeyName()));
    }

    /**
     * Parse request data key to actual data type to look up in {@code get/update/patch/delete} resource.
     *
     * @param pojo pojo
     * @return Actual primary key
     * @throws IllegalArgumentException if data key is invalid or missing
     * @since 1.0.0
     */
    default @NonNull K parseKey(@NonNull P pojo) throws IllegalArgumentException {
        return parseKey(Strings.toString(pojo.toJson().getValue(jsonKeyName())));
    }

    /**
     * Parse request data key to actual data type to look up in {@code get/update/patch/delete} resource.
     *
     * @param dataKey Request data key
     * @return Actual primary key
     * @throws IllegalArgumentException if data key is invalid or missing
     * @since 1.0.0
     */
    @NonNull K parseKey(@NonNull String dataKey) throws IllegalArgumentException;

    /**
     * Represents entity primary key is in {@code Integer} data type
     *
     * @param <P> Type of {@code JsonRecord}
     * @param <R> Type of {@code UpdatableRecord}
     * @see EntityMetadata
     * @since 1.0.0
     */
    interface SerialKeyEntity<P extends JsonRecord, R extends UpdatableRecord<R>>
        extends EntityMetadata<Integer, P, R> {

        default Integer parseKey(String dataKey) throws IllegalArgumentException {
            return Functions.toInt().apply(dataKey);
        }

    }


    /**
     * Represents entity primary key is in {@code Long} data type
     *
     * @param <P> Type of {@code JsonRecord}
     * @param <R> Type of {@code UpdatableRecord}
     * @see EntityMetadata
     * @since 1.0.0
     */
    interface BigSerialKeyEntity<P extends JsonRecord, R extends UpdatableRecord<R>>
        extends EntityMetadata<Long, P, R> {

        default Long parseKey(String dataKey) throws IllegalArgumentException {
            return Functions.toLong().apply(dataKey);
        }

    }


    /**
     * Represents entity primary key is in {@code String} data type
     *
     * @param <P> Type of {@code JsonRecord}
     * @param <R> Type of {@code UpdatableRecord}
     * @see EntityMetadata
     * @since 1.0.0
     */
    interface StringKeyEntity<P extends JsonRecord, R extends UpdatableRecord<R>> extends EntityMetadata<String, P, R> {

        default String parseKey(String dataKey) throws IllegalArgumentException {
            return dataKey;
        }

    }


    /**
     * Represents entity primary key is in {@code UUID} data type
     *
     * @param <P> Type of {@code JsonRecord}
     * @param <R> Type of {@code UpdatableRecord}
     * @see EntityMetadata
     * @since 1.0.0
     */
    interface UUIDKeyEntity<P extends JsonRecord, R extends UpdatableRecord<R>> extends EntityMetadata<UUID, P, R> {

        default UUID parseKey(String dataKey) throws IllegalArgumentException {
            return Functions.getOrThrow(() -> Functions.toUUID().apply(dataKey),
                                        t -> new IllegalArgumentException("Invalid key",
                                                                          t instanceof IllegalArgumentException
                                                                          ? new HiddenException(t)
                                                                          : t));
        }

    }


    interface UUID64KeyEntity<P extends JsonRecord, R extends UpdatableRecord<R>> extends EntityMetadata<String, P, R> {

        default String parseKey(String dataKey) throws IllegalArgumentException {
            return Functions.getOrThrow(() -> UUID64.uuid64ToUuidStr(dataKey),
                                        t -> new IllegalArgumentException("Invalid key",
                                                                          t instanceof IllegalArgumentException
                                                                          ? new HiddenException(t)
                                                                          : t));
        }

    }

}

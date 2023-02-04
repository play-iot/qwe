package cloud.playio.qwe.sql.validation;

import java.util.Optional;

import org.jooq.Field;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.exceptions.DataAlreadyExistException;
import cloud.playio.qwe.exceptions.DataBeingUsedException;
import cloud.playio.qwe.exceptions.DataNotFoundException;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.pojos.JsonPojo;
import cloud.playio.qwe.utils.JsonUtils;

import lombok.NonNull;

/**
 * Entity validation for request input
 *
 * @param <P> Type of {@code JsonRecord}
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public interface EntityValidation<P extends JsonRecord> {

    /**
     * The constant NOT_FOUND_MSG.
     */
    String NOT_FOUND_MSG = "Not found resource with {0}";
    /**
     * The constant ALREADY_EXISTED_MSG.
     */
    String ALREADY_EXISTED_MSG = "Already existed resource with {0}";
    /**
     * The constant RESOURCE_IS_USING_MSG.
     */
    String RESOURCE_IS_USING_MSG = "Resource with {0} is using by another resource";
    /**
     * The constant MANDATORY_MSG.
     */
    String MANDATORY_MSG = "{0} is mandatory";

    /**
     * Context entity metadata.
     *
     * @return the entity metadata
     * @see EntityMetadata
     * @since 1.0.0
     */
    EntityMetadata context();

    /**
     * Validate when creating new resource
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param reqData given request resource object
     * @return instance for fluent API
     * @throws IllegalArgumentException if any invalid parameter
     * @since 1.0.0
     */
    @NonNull
    default <PP extends P> PP onCreating(@NonNull RequestData reqData) throws IllegalArgumentException {
        return (PP) context().parseFromRequest(reqData.body());
    }

    /**
     * Validate when updating resource
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param dbData  existing resource object from database
     * @param reqData given request resource object
     * @return pojo instance for fluent API
     * @throws IllegalArgumentException if any invalid parameter
     * @since 1.0.0
     */
    @NonNull
    default <PP extends P> PP onUpdating(@NonNull P dbData, @NonNull RequestData reqData)
        throws IllegalArgumentException {
        final JsonObject body = reqData.body().copy();
        final Object key = Optional.ofNullable(body.remove(context().requestKeyName()))
                                   .orElse(body.remove(context().jsonKeyName()));
        body.put(context().jsonKeyName(), JsonData.checkAndConvert(context().parseKey(Strings.toString(key))));
        return (PP) context().parseFromRequest(body);
    }

    /**
     * Validate when patching resource
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param dbData  existing resource object from database
     * @param reqData given request resource object
     * @return pojo instance for fluent API
     * @throws IllegalArgumentException if any invalid parameter
     * @since 1.0.0
     */
    @NonNull
    default <PP extends P> PP onPatching(@NonNull P dbData, @NonNull RequestData reqData)
        throws IllegalArgumentException {
        final JsonObject body = reqData.body().copy();
        final Object key = Optional.ofNullable(body.remove(context().requestKeyName()))
                                   .orElse(body.remove(context().jsonKeyName()));
        body.put(context().jsonKeyName(), JsonData.checkAndConvert(context().parseKey(Strings.toString(key))));
        return (PP) context().parseFromRequest(JsonPojo.merge(dbData, body));
    }

    /**
     * Validate when deleting resource.
     *
     * @param <PP>    Type of {@code JsonRecord}
     * @param dbData  the db data
     * @param reqData the req data
     * @return the pp
     * @throws IllegalArgumentException the illegal argument exception
     * @since 1.0.0
     */
    default <PP extends P> PP onDeleting(@NonNull P dbData, @NonNull RequestData reqData)
        throws IllegalArgumentException {
        return (PP) dbData;
    }

    /**
     * Construct {@code NotFound exception} by {@code primary key}
     *
     * @param primaryKey Given primary key
     * @return not found exception
     * @see DataNotFoundException
     * @since 1.0.0
     */
    default DataNotFoundException notFound(@NonNull Object primaryKey) {
        return notFound(JsonUtils.kvMsg(context().requestKeyName(), primaryKey));
    }

    /**
     * Construct {@code NotFound exception} by {@code entity key}
     *
     * @param pojoKey Given pojo key value
     * @return not found exception
     * @see DataNotFoundException
     * @since 1.0.0
     */
    default DataNotFoundException notFound(@NonNull String pojoKey) {
        return new DataNotFoundException(Strings.format(NOT_FOUND_MSG, pojoKey));
    }

    /**
     * Construct {@code AlreadyExist exception} by {@code entity key}
     *
     * @param pojoKey Given primary key
     * @return already exist exception
     * @see DataAlreadyExistException
     * @since 1.0.0
     */
    default DataAlreadyExistException alreadyExisted(String pojoKey) {
        return new DataAlreadyExistException(Strings.format(ALREADY_EXISTED_MSG, pojoKey));
    }

    /**
     * Construct {@code BeingUsedException exception} by {@code entity key}
     *
     * @param pojoKey Given pojo key value
     * @return already exist exception
     * @see DataBeingUsedException
     * @since 1.0.0
     */
    default DataBeingUsedException unableDeleteDueUsing(String pojoKey) {
        return new DataBeingUsedException(Strings.format(RESOURCE_IS_USING_MSG, pojoKey));
    }

    /**
     * Construct {@code IllegalArgumentException exception} by mandatory field.
     *
     * @param field the table field
     * @return the illegal argument exception
     * @since 1.0.0
     */
    default IllegalArgumentException mandatoryField(@NonNull String field) {
        return new IllegalArgumentException(Strings.format(MANDATORY_MSG, field));
    }

    /**
     * Construct {@code IllegalArgumentException exception} by mandatory field.
     *
     * @param field the field
     * @return the illegal argument exception
     * @since 1.0.0
     */
    default IllegalArgumentException mandatoryField(@NonNull Field field) {
        return mandatoryField(context().table().getJsonField(field));
    }

}

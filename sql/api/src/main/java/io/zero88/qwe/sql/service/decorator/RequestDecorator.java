package io.zero88.qwe.sql.service.decorator;

import io.zero88.qwe.dto.msg.RequestData;

import lombok.NonNull;

/**
 * Represents a decorator that make up {@code request data} depends on {@code entity resource}
 *
 * @since 1.0.0
 */
public interface RequestDecorator {

    /**
     * Decorates request data on creating one resource.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData onCreatingOneResource(@NonNull RequestData requestData) {
        return requestData;
    }

    /**
     * Decorates request data on modifying one resource.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData onModifyingOneResource(@NonNull RequestData requestData) {
        return requestData;
    }

    /**
     * Decorates request data on deleting one resource.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData onDeletingOneResource(@NonNull RequestData requestData) {
        return requestData;
    }

    /**
     * Decorates request data on reading many resource.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData onReadingManyResource(@NonNull RequestData requestData) {
        return requestData;
    }

    /**
     * Decorates request data on reading one resource request data.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData onReadingOneResource(@NonNull RequestData requestData) {
        return requestData;
    }

}

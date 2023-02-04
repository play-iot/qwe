package cloud.playio.qwe.sql.service.decorator;

import java.util.List;

import cloud.playio.qwe.dto.msg.RequestData;

import lombok.NonNull;

/**
 * Represents for decorator extension that optimizes request data by {@code default} value or {@code alias} value
 *
 * @since 1.0.0
 */
public interface RequestDecoratorExtension extends RequestDecorator {

    /**
     * Defines the wrapped request decorator.
     *
     * @return the request decorator
     * @since 1.0.0
     */
    @NonNull RequestDecorator decorator();

    /**
     * Declares the priority keys in request body. Request body will be updated only a first catching item.
     *
     * @return the priority body keys
     * @since 1.0.0
     */
    @NonNull List<String> bodyKeys();

    /**
     * Declares the priority keys in request filter. Request filter will be updated only a first catching item.
     *
     * @return the priority filter keys
     * @since 1.0.0
     */
    @NonNull List<String> filterKeys();

    @Override
    @NonNull
    default RequestData onCreatingOneResource(@NonNull RequestData requestData) {
        return decorator().onCreatingOneResource(optimizeRequestBody(requestData));
    }

    @Override
    @NonNull
    default RequestData onModifyingOneResource(@NonNull RequestData requestData) {
        return decorator().onModifyingOneResource(optimizeRequestBody(requestData));
    }

    @Override
    @NonNull
    default RequestData onDeletingOneResource(@NonNull RequestData requestData) {
        return decorator().onDeletingOneResource(optimizeRequestBody(requestData));
    }

    @Override
    @NonNull
    default RequestData onReadingManyResource(@NonNull RequestData requestData) {
        return decorator().onReadingManyResource(optimizeRequestBody(optimizeRequestFilter(requestData)));
    }

    @Override
    @NonNull
    default RequestData onReadingOneResource(@NonNull RequestData requestData) {
        return decorator().onReadingOneResource(optimizeRequestBody(optimizeRequestFilter(requestData)));
    }

    /**
     * Optimize request body.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    default @NonNull RequestData optimizeRequestBody(@NonNull RequestData requestData) {
        return requestData;
    }

    /**
     * Optimize request filter.
     *
     * @param requestData the request data
     * @return the request data
     * @since 1.0.0
     */
    default @NonNull RequestData optimizeRequestFilter(@NonNull RequestData requestData) {
        return requestData;
    }

}

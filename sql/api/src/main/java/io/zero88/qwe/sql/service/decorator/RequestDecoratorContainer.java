package cloud.playio.qwe.sql.service.decorator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import cloud.playio.qwe.dto.msg.RequestData;

import lombok.NonNull;

/**
 * Represents a container that contains a list of request decorators.
 *
 * @see RequestDecorator
 * @since 1.0.0
 */
public interface RequestDecoratorContainer extends RequestDecorator {

    /**
     * Create request decorator container.
     *
     * @param decorators the decorators
     * @return the request decorator container
     * @since 1.0.0
     */
    static RequestDecoratorContainer create(RequestDecorator... decorators) {
        return () -> Arrays.asList(decorators);
    }

    /**
     * Declares the decorators
     *
     * @return the decorators
     * @since 1.0.0
     */
    @NonNull List<RequestDecorator> decorators();

    @Override
    default @NonNull RequestData onCreatingOneResource(@NonNull RequestData requestData) {
        return optimize(requestData, RequestDecorator::onCreatingOneResource);
    }

    @Override
    default @NonNull RequestData onModifyingOneResource(@NonNull RequestData requestData) {
        return optimize(requestData, RequestDecorator::onModifyingOneResource);
    }

    @Override
    default @NonNull RequestData onDeletingOneResource(@NonNull RequestData requestData) {
        return optimize(requestData, RequestDecorator::onDeletingOneResource);
    }

    @Override
    default @NonNull RequestData onReadingManyResource(@NonNull RequestData requestData) {
        return optimize(requestData, RequestDecorator::onReadingManyResource);
    }

    @Override
    default @NonNull RequestData onReadingOneResource(@NonNull RequestData requestData) {
        return optimize(requestData, RequestDecorator::onReadingOneResource);
    }

    /**
     * Optimize request data.
     *
     * @param requestData the request data
     * @param optimizer   the optimizer
     * @return the request data
     * @since 1.0.0
     */
    @NonNull
    default RequestData optimize(@NonNull RequestData requestData,
                                 @NonNull BiFunction<RequestDecorator, RequestData, RequestData> optimizer) {
        return decorators().stream()
                           .filter(Objects::nonNull)
                           .map(decorator -> optimizer.apply(decorator, requestData))
                           .reduce((r1, r2) -> r2)
                           .orElse(requestData);
    }

}

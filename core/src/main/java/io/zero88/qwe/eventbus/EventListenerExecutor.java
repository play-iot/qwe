package io.zero88.qwe.eventbus;

import java.util.Collection;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.auth.SecurityInterceptor;
import io.zero88.qwe.dto.JsonDataSerializer;
import io.zero88.qwe.eventbus.output.OutputToFuture;
import io.zero88.qwe.eventbus.output.OutputToFutureServiceLoader;
import io.zero88.qwe.eventbus.refl.EventAnnotationProcessor;
import io.zero88.qwe.eventbus.refl.EventParameterParser;

import lombok.NonNull;

/**
 * Represents for the reflection executor that receives an incoming message in {@code EventBus} then based on a received
 * {@code event action}, dispatch a message to an appropriate {@code Method} in {@link EventListener}
 *
 * @since 1.0.0
 */
public interface EventListenerExecutor extends HasSharedData {

    static EventListenerExecutor create(@NonNull EventListener listener, @NonNull SharedDataLocalProxy sharedData) {
        return new EventListenerExecutorImpl(listener, sharedData);
    }

    /**
     * The {@code EventBus listener} in current context
     *
     * @return the listener
     * @see EventListener
     */
    @NotNull EventListener listener();

    /**
     * The converter from an incoming {@link Message} to {@link EventMessage}
     *
     * @return the message converter
     * @see EventBusMessageConverter
     */
    default @NotNull EventBusMessageConverter messageConverter() {
        return EventBusMessageConverter.DEFAULT;
    }

    /**
     * The event annotation processor to extract list of {@code parameters} in {@code method}
     *
     * @return the event annotation processor
     * @see EventAnnotationProcessor
     */
    default @NotNull EventAnnotationProcessor annotationProcessor() {
        return EventAnnotationProcessor.DEFAULT;
    }

    /**
     * The parser that extract the {@code event message} to relevant a {@code method param}
     *
     * @return the param parser
     * @see EventParameterParser
     */
    default @NotNull EventParameterParser paramParser() {
        return EventParameterParser.create(sharedData(), listener().mapper());
    }

    /**
     * List of handlers that cast or wrap a reply data in any type into a {@code Vertx} {@link Future}
     *
     * @return the handlers
     */
    @SuppressWarnings("rawtypes")
    default @NotNull Collection<OutputToFuture> outputToFuture() {
        return OutputToFutureServiceLoader.getInstance().getHandlers();
    }

    /**
     * The serializer that converts a reply data in any type to a {@link JsonObject}
     *
     * @return the output serializer
     */
    default @NotNull Function<Object, JsonObject> outputSerializer() {
        return JsonDataSerializer.builder()
                                 .mapper(listener().mapper())
                                 .backupKey(listener().fallback())
                                 .lenient(true)
                                 .build();
    }

    /**
     * The listener security interceptor
     *
     * @return the listener security interceptor
     * @see SecurityInterceptor
     */
    default SecurityInterceptor securityInterceptor() {
        return SecurityInterceptor.DEFAULT;
    }

    /**
     * Dispatch message to the event listener
     *
     * @param message the incoming message
     * @return the event message in Future
     */
    Future<EventMessage> execute(Message message);

}

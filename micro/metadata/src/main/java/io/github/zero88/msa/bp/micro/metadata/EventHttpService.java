package io.github.zero88.msa.bp.micro.metadata;

import java.util.Set;

import io.github.zero88.msa.bp.event.EventListener;

/**
 * Represents Event HTTP service.
 *
 * @since 1.0.0
 */
public interface EventHttpService extends EventListener {

    /**
     * Declares {@code API Service} name.
     *
     * @return service name
     * @since 1.0.0
     */
    String api();

    /**
     * Declares Eventbus Address.
     *
     * @return service address. Default: {@code current class full-qualified name}
     * @since 1.0.0
     */
    default String address() {
        return this.getClass().getName();
    }

    /**
     * Declares router definitions.
     *
     * @return router mapping between {@code EventAction} and {@code HttpMethod}
     * @see EventMethodDefinition
     * @since 1.0.0
     */
    Set<EventMethodDefinition> definitions();

}

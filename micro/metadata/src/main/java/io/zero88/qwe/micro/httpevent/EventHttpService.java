package io.zero88.qwe.micro.httpevent;

import java.util.Set;

import io.zero88.qwe.eventbus.EventBusListener;

/**
 * Represents Event HTTP service.
 *
 * @since 1.0.0
 */
public interface EventHttpService extends EventBusListener {

    /**
     * Declares {@code API Service} name.
     *
     * @return service name
     * @since 1.0.0
     */
    String api();

    /**
     * Declares EventBus Address.
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

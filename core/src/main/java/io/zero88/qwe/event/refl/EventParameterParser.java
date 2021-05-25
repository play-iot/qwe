package io.zero88.qwe.event.refl;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EBContext;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.exceptions.ImplementationError;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

public interface EventParameterParser {

    static EventParameterParser create(SharedDataLocalProxy sharedData, ObjectMapper mapper) {
        return new EventParameterParserImpl(sharedData, mapper);
    }

    /**
     * Extract event message to an array of param's value
     *
     * @param message Event message
     * @param params  Method params
     * @return An array param value
     * @throws ImplementationError      if binding unsupported {@link EBContext} data type
     * @throws IllegalArgumentException if a registered parameter is unable deserialized from runtime event message
     */
    @NonNull Object[] extract(EventMessage message, MethodParam[] params);

}

package io.zero88.qwe.event.refl;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface EventParameterParser {

    static EventParameterParser create(SharedDataLocalProxy sharedData, ObjectMapper mapper) {
        return new EventParameterParserImpl(sharedData, mapper);
    }

    Object[] extract(EventMessage message, MethodParam[] params);

}

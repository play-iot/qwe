package cloud.playio.qwe.eventbus.refl;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EBContext;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.exceptions.ImplementationError;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

public interface EventParameterParser {

    static EventParameterParser create(SharedDataLocalProxy sharedData, ObjectMapper mapper) {
        return new EventParameterParserImpl().setup(sharedData, mapper);
    }

    /**
     * Setup event parameter parser
     *
     * @param sharedData shared data
     * @param mapper     mapper
     * @return a reference to this for fluent API
     */
    EventParameterParser setup(SharedDataLocalProxy sharedData, ObjectMapper mapper);

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

package io.zero88.qwe.event.refl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.DataTransferObject.StandardKey;
import io.zero88.qwe.event.EBBody;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.ImplementationError;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventParameterParserImpl implements EventParameterParser {

    private final SharedDataLocalProxy localDataProxy;
    private final ObjectMapper mapper;

    @Override
    public Object[] extract(EventMessage message, MethodParam[] params) {
        if (params.length == 0) {
            return new Object[] {};
        }
        if (params.length == 2) {
            List<String> paramNames = Arrays.stream(params).map(MethodParam::getParamName).collect(Collectors.toList());
            if (paramNames.contains("data") && paramNames.contains("error")) {
                return "data".equals(paramNames.get(0))
                       ? new Object[] {message.getData(), message.getError()}
                       : new Object[] {message.getError(), message.getData()};
            }
        }
        boolean isOne = params.length - Arrays.stream(params).filter(MethodParam::isContext).count() == 1;
        return Arrays.stream(params)
                     .map(param -> param.isContext()
                                   ? parseEBContext(message.getAction(), param)
                                   : parseEBParam(message, param, isOne))
                     .toArray();
    }

    protected Object parseEBContext(EventAction action, MethodParam param) {
        if (param.getParamClass() == EventAction.class) {
            return action;
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), Vertx.class)) {
            return localDataProxy.getVertx();
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), SharedDataLocalProxy.class)) {
            return localDataProxy;
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), EventBusClient.class)) {
            return EventBusClient.create(localDataProxy);
        }
        throw new ImplementationError(ErrorCode.UNSUPPORTED,
                                      "Unsupported EventBus context [" + param.getParamClass() + "]");
    }

    protected Object parseEBParam(EventMessage message, MethodParam param, boolean oneParam) {
        JsonObject data = message.isError() ? Optional.ofNullable(message.getError())
                                                      .map(ErrorMessage::toJson)
                                                      .orElse(null) : message.getData();
        if (Objects.isNull(data)) {
            return null;
        }
        Object d = lookupParamValue(param, data);
        if (Objects.isNull(d)) {
            if (param.getParamClass().isPrimitive()) {
                throw new IllegalArgumentException(
                    "Data Field [" + param.getParamName() + "] is primitive type but given null data");
            }
            return oneParam ? tryDeserialize(data.getMap(), param.getParamClass()) : null;
        }
        return tryParseParamValue(param.getParamClass(), d);
    }

    protected Object lookupParamValue(MethodParam param, JsonObject data) {
        EBBody a = param.lookupAnnotation(EBBody.class);
        if (Objects.nonNull(a) && data.containsKey(StandardKey.BODY)) {
            return Functions.getIfThrow(() -> JsonObject.mapFrom(data.getValue(StandardKey.BODY)))
                            .map(json -> json.getValue(a.value()))
                            .orElse(null);
        }
        return data.getValue(Optional.ofNullable(a).map(EBBody::value).orElseGet(param::getParamName));
    }

    protected Object tryParseParamValue(Class<?> paramClass, Object d) {
        try {
            if (ReflectionClass.isJavaLangObject(paramClass)) {
                return ReflectionClass.assertDataType(d.getClass(), paramClass) ? d : paramClass.cast(d);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Event message format is invalid", new HiddenException(
                "Unable cast data type [" + paramClass.getName() + "]", e));
        }
        return tryDeserialize(d, paramClass);
    }

    private Object tryDeserialize(Object data, Class<?> paramClass) {
        try {
            return mapper.convertValue(data, paramClass);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Event message format is invalid",
                                               new HiddenException("Jackson parser error", e));
        }
    }

}

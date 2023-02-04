package cloud.playio.qwe.eventbus.refl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Functions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.auth.UserInfo;
import cloud.playio.qwe.dto.ErrorMessage;
import cloud.playio.qwe.dto.msg.DataTransferObject.StandardKey;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.ImplementationError;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventParameterParserImpl implements EventParameterParser {

    private SharedDataLocalProxy sharedData;
    private ObjectMapper mapper;

    @Override
    public EventParameterParser setup(SharedDataLocalProxy sharedData, ObjectMapper mapper) {
        this.sharedData = sharedData;
        this.mapper = mapper;
        return this;
    }

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
        boolean isOne = params.length - Arrays.stream(params).filter(MethodParam::isEBContext).count() == 1;
        return Arrays.stream(params)
                     .map(param -> param.isEBContext()
                                   ? parseEBContext(message, param)
                                   : parseEBParam(message, param, isOne))
                     .toArray();
    }

    protected Object parseEBContext(EventMessage message, MethodParam param) {
        if (param.getParamClass() == EventAction.class) {
            return message.getAction();
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), Vertx.class)) {
            return sharedData.getVertx();
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), SharedDataLocalProxy.class)) {
            return sharedData;
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), EventBusClient.class)) {
            return EventBusClient.create(sharedData);
        }
        if (ReflectionClass.assertDataType(param.getParamClass(), UserInfo.class)) {
            return message.getUserInfo();
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
            if (param.isPrimitive()) {
                throw new IllegalArgumentException(
                    "Data Field [" + param.getParamName() + "] is primitive type but given null data");
            }
            return oneParam ? tryDeserialize(data.getMap(), param) : null;
        }
        return tryParseParamValue(param, d);
    }

    protected Object lookupParamValue(MethodParam param, JsonObject data) {
        if (param.isEBBody() && data.containsKey(StandardKey.BODY)) {
            return Functions.getIfThrow(() -> JsonObject.mapFrom(data.getValue(StandardKey.BODY)))
                            .map(json -> "".equals(param.getParamName()) ? json : json.getValue(param.getParamName()))
                            .orElse(null);
        }
        return data.getValue(param.getParamName());
    }

    protected Object tryParseParamValue(MethodParam param, Object d) {
        final Class<?> paramClass = param.getParamClass();
        try {
            if (!param.isArray() && ReflectionClass.isJavaLangObject(paramClass)) {
                return ReflectionClass.assertDataType(d.getClass(), paramClass) ? d : paramClass.cast(d);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Event message format is invalid", new HiddenException(
                "Unable cast data type [" + paramClass.getName() + "]", e));
        }
        return tryDeserialize(d, param);
    }

    protected Object tryDeserialize(Object data, MethodParam param) {
        try {
            final JavaType javaType = param.toJavaType(mapper);
            if (javaType == null) {
                return mapper.convertValue(data, param.getParamClass());
            }
            return mapper.convertValue(data, javaType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Event message body is invalid",
                                               new HiddenException("Jackson parser error", e));
        }
    }

}

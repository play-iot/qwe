package io.zero88.qwe.event;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.event.EventContractor.Param;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.UnsupportedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventAnnotationProcessorImpl implements EventAnnotationProcessor {

    private final ScanResult scanResult;

    @Override
    public MethodInfo find(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action) {
        ClassInfo classInfo = scanResult.getClassInfo(listenerClass.getName());
        return classInfo.getMethodInfo()
                        .stream()
                        .filter(m -> filterMethodByAction(m, action))
                        .findFirst()
                        .orElseThrow(() -> new UnsupportedException("Unsupported event [" + action + "]"));
    }

    @Override
    public Object[] extract(@NonNull ObjectMapper mapper, @NonNull EventMessage message,
                            MethodParameterInfo[] parameters) {
        if (Objects.isNull(parameters) || parameters.length == 0) {
            return new Object[] {};
        }
        if (parameters.length == 2) {
            List<String> paramNames = Arrays.stream(parameters).map(this::lookupParamName).collect(Collectors.toList());
            if (paramNames.contains("data") && paramNames.contains("error")) {
                return "data".equals(paramNames.get(0))
                       ? new Object[] {message.getData(), message.getError()}
                       : new Object[] {message.getError(), message.getData()};
            }
        }
        return Arrays.stream(parameters)
                     .map(entry -> parseParamValue(parseMessage(message), entry, parameters.length == 1, mapper))
                     .toArray();
    }

    @Override
    public void close() {
        this.scanResult.close();
    }

    protected boolean filterMethodByAction(MethodInfo methodInfo, EventAction action) {
        final AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(EventContractor.class.getName());
        if (annotationInfo == null) {
            return false;
        }
        EventContractor contractor = (EventContractor) annotationInfo.loadClassAndInstantiate();
        return Arrays.stream(contractor.action()).anyMatch(a -> a.equals(action.action()));
    }

    protected JsonObject parseMessage(EventMessage message) {
        JsonObject data = message.isError() ? Optional.ofNullable(message.getError())
                                                      .map(ErrorMessage::toJson)
                                                      .orElse(null) : message.getData();
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException(Strings.format("Event Message Data is null: {0}", message.toJson()));
        }
        return data;
    }

    protected Object parseParamValue(JsonObject data, MethodParameterInfo param, boolean oneParam,
                                     ObjectMapper mapper) {
        String paramName = lookupParamName(param);
        Class<?> paramClass = loadClass(param.getTypeSignatureOrTypeDescriptor());
        Object d = data.getValue(paramName);
        if (Objects.isNull(d)) {
            return oneParam ? tryDeserialize(data.getMap(), mapper, paramClass) : null;
        }
        return tryParseParamValue(mapper, paramClass, d);
    }

    protected String lookupParamName(MethodParameterInfo param) {
        final AnnotationInfo annotationInfo = param.getAnnotationInfo(Param.class.getName());
        if (Objects.isNull(annotationInfo)) {
            return Optional.ofNullable(param.getName()).orElse("");
        }
        return (String) annotationInfo.getParameterValues().getValue("value");
    }

    protected Object tryParseParamValue(ObjectMapper mapper, Class<?> paramClass, Object d) {
        try {
            if (ReflectionClass.isJavaLangObject(paramClass)) {
                return ReflectionClass.assertDataType(d.getClass(), paramClass) ? d : paramClass.cast(d);
            }
        } catch (ClassCastException e) {
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid",
                                    new HiddenException("Unable cast data type [" + paramClass.getName() + "]", e));
        }
        return tryDeserialize(d, mapper, paramClass);
    }

    private Object tryDeserialize(Object data, ObjectMapper mapper, Class<?> paramClass) {
        try {
            return mapper.convertValue(data, paramClass);
        } catch (IllegalArgumentException e) {
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid",
                                    new HiddenException("Jackson parser error", e));
        }
    }

    protected Class<?> loadClass(TypeSignature signature) {
        if (signature instanceof ArrayTypeSignature) {
            return ((ArrayTypeSignature) signature).loadClass();
        } else if (signature instanceof BaseTypeSignature) {
            return ((BaseTypeSignature) signature).getType();
        } else if (signature instanceof ClassRefTypeSignature) {
            return ((ClassRefTypeSignature) signature).loadClass();
        } else if (signature instanceof TypeVariableSignature) {
            TypeVariableSignature typeVariableSignature = (TypeVariableSignature) signature;
            TypeParameter typeParameter = typeVariableSignature.resolve();
            return typeParameter.getClass();
        }
        return null;
    }

}

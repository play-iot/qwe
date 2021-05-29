package io.zero88.qwe.event.refl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import io.zero88.qwe.event.EBContext;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EBParam;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.UnsupportedException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAnnotationProcessor implements EventAnnotationProcessor {

    private final String[] ignorePackages;

    @Override
    public MethodMeta lookup(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action) {
        return find(listenerClass, action);
    }

    protected MethodMeta find(@NonNull Class<?> listenerClass, @NonNull EventAction action) {
        if (Arrays.stream(ignorePackages).anyMatch(s -> listenerClass.getPackage().getName().startsWith(s))) {
            throw new UnsupportedException("Unsupported event [" + action + "]");
        }
        return Arrays.stream(listenerClass.getMethods())
                     .filter(m -> filterMethodByEBContract(m, action))
                     .reduce((m1, m2) -> {
                         if (m1.getDeclaringClass().equals(m2.getDeclaringClass())) {
                             throw new ImplementationError(ErrorCode.CONFLICT_ERROR,
                                                           "More than one event [" + action + "]");
                         }
                         return m1;
                     })
                     .map(this::analyze)
                     .orElseGet(() -> find(listenerClass.getSuperclass(), action));
    }

    protected boolean filterMethodByEBContract(Method method, EventAction action) {
        final EBContract annotation = method.getAnnotation(EBContract.class);
        if (Objects.isNull(annotation)) {
            return false;
        }
        return Arrays.stream(annotation.action()).anyMatch(s -> action.action().equals(s));
    }

    protected MethodMeta analyze(Method method) {
        return new MethodMetaImpl(method.getDeclaringClass().getName(), method, analyzeParams(method.getParameters()));
    }

    protected MethodParam[] analyzeParams(Parameter[] params) {
        if (params.length == 0) {
            return new MethodParam[] {};
        }
        return Arrays.stream(params)
                     .map(param -> new MethodParam(lookupParamName(param), param.getType(),
                                                   param.isAnnotationPresent(EBContext.class)))
                     .toArray(MethodParam[]::new);
    }

    protected String lookupParamName(Parameter param) {
        return Optional.ofNullable(param.getAnnotation(EBParam.class))
                       .map(EBParam::value)
                       .orElseGet(() -> Optional.ofNullable(param.getName()).orElse(""));
    }

}

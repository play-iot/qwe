package cloud.playio.qwe.eventbus.refl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.ImplementationError;
import cloud.playio.qwe.exceptions.UnsupportedException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAnnotationProcessor implements EventAnnotationProcessor {

    private final String[] ignorePackages;
    private final Set<Class<? extends Annotation>> supportedAnnotations;

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
                     .map(param -> MethodParam.create(param, supportedAnnotations))
                     .toArray(MethodParam[]::new);
    }

}

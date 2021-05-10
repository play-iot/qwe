package io.zero88.qwe.event.refl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import io.vertx.core.Future;
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
public class EventAnnotationProcessorImpl implements EventAnnotationProcessor {

    private final String[] ignorePackages;

    @Override
    public MethodMeta lookup(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action) {
        try (ScanResult scanResult = new ClassGraph().enableClassInfo()
                                                     .enableAnnotationInfo()
                                                     .enableMethodInfo()
                                                     .rejectPackages(ignorePackages)
                                                     .scan()) {

            return scanResult.getClassInfo(listenerClass.getName())
                             .getMethodInfo()
                             .stream()
                             .filter(m -> filterMethodByAction(m, action))
                             .reduce((m1, m2) -> {
                                 if (m1.getClassName().equals(m2.getClassName())) {
                                     throw new ImplementationError(ErrorCode.CONFLICT_ERROR,
                                                                   "More than one event [" + action + "]");
                                 }
                                 return m1;
                             })
                             .map(this::analyze)
                             .orElseThrow(() -> new UnsupportedException("Unsupported event [" + action + "]"));
        }
    }

    protected boolean filterMethodByAction(MethodInfo methodInfo, EventAction action) {
        final AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(EBContract.class.getName());
        if (annotationInfo == null) {
            return false;
        }
        EBContract contractor = (EBContract) annotationInfo.loadClassAndInstantiate();
        return Arrays.stream(contractor.action()).anyMatch(a -> a.equals(action.action()));
    }

    protected MethodMeta analyze(@NonNull MethodInfo methodInfo) {
        final TypeSignature resultType = methodInfo.getTypeSignatureOrTypeDescriptor().getResultType();
        return new MethodMetaImpl(methodInfo.getClassName(), methodInfo.loadClassAndGetMethod(), isVoid(resultType),
                                  isFuture(resultType), analyzeParams(methodInfo.getParameterInfo()));
    }

    protected MethodParam[] analyzeParams(MethodParameterInfo[] params) {
        if (params.length == 0) {
            return new MethodParam[] {};
        }
        return Arrays.stream(params)
                     .map(param -> new MethodParam(lookupParamName(param),
                                                   loadClass(param.getTypeSignatureOrTypeDescriptor()),
                                                   Objects.nonNull(getAnnotation(param, EBContext.class))))
                     .toArray(MethodParam[]::new);
    }

    protected String lookupParamName(MethodParameterInfo param) {
        final AnnotationInfo annotationInfo = getAnnotation(param, EBParam.class);
        return Objects.isNull(annotationInfo)
               ? Optional.ofNullable(param.getName()).orElse("")
               : (String) annotationInfo.getParameterValues().getValue("value");
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
        throw new UnsupportedException(
            "Unknown signature [" + Optional.ofNullable(signature).map(TypeSignature::getClass).orElse(null) + "]");
    }

    protected boolean isVoid(TypeSignature signature) {
        if (signature instanceof BaseTypeSignature) {
            return ((BaseTypeSignature) signature).getTypeSignatureChar() == 'V';
        }
        if (signature instanceof ClassRefTypeSignature) {
            return Void.class.getName().equals(((ClassRefTypeSignature) signature).getBaseClassName());
        }
        return false;
    }

    protected boolean isFuture(TypeSignature signature) {
        if (!(signature instanceof ClassRefTypeSignature)) {
            return false;
        }
        final ClassRefTypeSignature refSign = (ClassRefTypeSignature) signature;
        return refSign.getBaseClassName().equals(Future.class.getName())/* &&
               refSign.getTypeArguments().stream().findFirst().map(s -> isVoid(s.getTypeSignature())).orElse(false)*/;
    }

    private AnnotationInfo getAnnotation(MethodParameterInfo param, Class<? extends Annotation> annotationClass) {
        return param.getAnnotationInfo(annotationClass.getName());
    }

}

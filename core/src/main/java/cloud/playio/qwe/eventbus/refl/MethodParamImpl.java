package cloud.playio.qwe.eventbus.refl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBParam;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

public final class MethodParamImpl implements MethodParamInternal {

    @Getter
    private final String paramName;
    @Getter
    private final Type paramType;
    @Getter
    private final Map<Class<? extends Annotation>, Annotation> annotations;
    private Class<?> _paramClass;

    public MethodParamImpl(Parameter parameter, Set<Class<? extends Annotation>> supportedAnnotations) {
        paramType = parameter.getParameterizedType();
        annotations = supportedAnnotations.stream()
                                          .map(parameter::getAnnotation)
                                          .filter(Objects::nonNull)
                                          .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
        paramName = Optional.ofNullable(parameter.getAnnotation(EBBody.class))
                            .map(EBBody::value)
                            .orElseGet(() -> Optional.ofNullable(parameter.getAnnotation(EBParam.class))
                                                     .map(EBParam::value)
                                                     .orElse(parameter.getName()));
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T lookupAnnotation(Class<T> annotationClass) {
        return (T) annotations.get(annotationClass);
    }

    public Class<?> getParamClass() {
        if (_paramClass != null) {
            return _paramClass;
        }
        if (paramType instanceof Class) {
            return _paramClass = (Class<?>) paramType;
        }
        if (isParameterizedType()) {
            return _paramClass = loadClass(((ParameterizedType) paramType).getRawType().getTypeName());
        }
        return _paramClass = loadClass(paramType.getTypeName());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @Nullable JavaType toJavaType(ObjectMapper mapper) {
        if (isMap()) {
            final Class<?>[] types = Objects.requireNonNull(getMapComponentType());
            return mapper.getTypeFactory().constructMapType(LinkedHashMap.class, types[0], types[1]);
        }
        if (isCollection()) {
            return mapper.getTypeFactory()
                         .constructCollectionType((Class<? extends Collection>) getParamClass(),
                                                  getArrayOrCollectionComponentType());
        }
        if (isArray()) {
            return mapper.getTypeFactory().constructArrayType(getArrayOrCollectionComponentType());
        }
        if (isParameterizedType()) {
            return mapper.getTypeFactory().constructParametricType(getParamClass(), getComponentTypeClasses());
        }
        return null;
    }

    public Class<?> getArrayOrCollectionComponentType() {
        if (isCollection()) {
            return loadClass(((ParameterizedType) paramType).getActualTypeArguments()[0].getTypeName());
        }
        if (isArray()) {
            return ((Class<?>) paramType).getComponentType();
        }
        return null;
    }

    public Class<?>[] getMapComponentType() {
        if (isMap()) {
            final Type[] typeArguments = ((ParameterizedType) paramType).getActualTypeArguments();
            return new Class<?>[] {
                loadClass(typeArguments[0].getTypeName()), loadClass(typeArguments[1].getTypeName())
            };
        }
        return null;
    }

    public Class<?>[] getComponentTypeClasses() {
        return Optional.ofNullable(getComponentTypes())
                       .map(types -> Arrays.stream(types).map(t -> loadClass(t.getTypeName())).toArray(Class[]::new))
                       .orElse(null);
    }

    private Type[] getComponentTypes() {
        if (isParameterizedType()) {
            return ((ParameterizedType) paramType).getActualTypeArguments();
        }
        return null;
    }

    private Class<?> loadClass(String clsName) {
        return Objects.requireNonNull(ReflectionClass.findClass(clsName), "Not found class [" + clsName + "]");
    }

}

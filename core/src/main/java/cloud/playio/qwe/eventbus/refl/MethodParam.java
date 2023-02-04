package cloud.playio.qwe.eventbus.refl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBContext;
import cloud.playio.qwe.eventbus.EBParam;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface MethodParam {

    static MethodParam create(Parameter parameter, Set<Class<? extends Annotation>> supportedAnnotations) {
        return new MethodParamImpl(parameter, supportedAnnotations);
    }

    String getParamName();

    Class<?> getParamClass();

    <T extends Annotation> T lookupAnnotation(Class<T> annotationClass);

    /**
     * Check whether method param is EBParam or not
     *
     * @see EBParam
     */
    default boolean isEBParam() {
        return Objects.nonNull(lookupAnnotation(EBParam.class));
    }

    /**
     * Check whether method param is EBBody or not
     *
     * @see EBBody
     */
    default boolean isEBBody() {
        return Objects.nonNull(lookupAnnotation(EBBody.class));
    }

    /**
     * Check whether method param is EBContext or not
     *
     * @see EBContext
     */
    default boolean isEBContext() {
        return Objects.nonNull(lookupAnnotation(EBContext.class));
    }

    default boolean isCollection() {
        return ReflectionClass.assertDataType(getParamClass(), Collection.class);
    }

    default boolean isArray() {
        return getParamClass().isArray();
    }

    default boolean isMap() {
        return ReflectionClass.assertDataType(getParamClass(), Map.class);
    }

    default boolean isPrimitive() {
        return getParamClass().isPrimitive();
    }

    /**
     * Check whether param is parameterized type or not
     *
     * @see ParameterizedType
     */
    boolean isParameterizedType();

    /**
     * To jackson java type
     *
     * @param mapper Jackson object mapper
     * @return the java type, {@code nullable} if {@link #isParameterizedType()} is {@code false}
     */
    @Nullable JavaType toJavaType(ObjectMapper mapper);

}

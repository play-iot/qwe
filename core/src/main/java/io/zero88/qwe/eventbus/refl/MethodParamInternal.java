package io.zero88.qwe.eventbus.refl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

interface MethodParamInternal extends MethodParam {

    Type getParamType();

    Class<?> getArrayOrCollectionComponentType();

    Class<?>[] getMapComponentType();

    Class<?>[] getComponentTypeClasses();

    @Override
    default boolean isParameterizedType() {
        return getParamType() instanceof ParameterizedType;
    }

}

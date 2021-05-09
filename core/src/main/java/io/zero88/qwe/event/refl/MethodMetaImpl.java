package io.zero88.qwe.event.refl;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
final class MethodMetaImpl implements MethodMeta {

    private final String declaringClass;
    private final Method method;
    private final boolean outputIsVoid;
    private final boolean outputIsVertxFuture;
    private final MethodParam[] params;

    @Override
    public Method toMethod() {
        return method;
    }

}

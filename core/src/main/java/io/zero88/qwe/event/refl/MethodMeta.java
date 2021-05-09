package io.zero88.qwe.event.refl;

import java.lang.reflect.Method;

public interface MethodMeta {

    /**
     * @return the declaring class
     */
    String declaringClass();

    boolean outputIsVoid();

    boolean outputIsVertxFuture();

    MethodParam[] params();

    Method toMethod();

}

package io.zero88.qwe.event.refl;

import java.lang.reflect.Method;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface EventAnnotationProcessor {

    String[] IGNORE_PACKAGES = {
        "java", "jdk", "sun", "com.fasterxml", "ch.qos.logback", "org.apache", "org.junit", "org.gradle", "io.vertx",
        "io.reactivex", "io.netty"
    };

    static EventAnnotationProcessor create() {
        return create(IGNORE_PACKAGES);
    }

    static EventAnnotationProcessor create(String[] ignorePackages) {
        return new EventAnnotationProcessorImpl(ignorePackages);
    }

    MethodMeta scan(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action);

    interface MethodMeta {

        /**
         * @return the declaring class
         */
        String declaringClass();

        boolean outputIsVoid();

        boolean outputIsVertxFuture();

        MethodParam[] params();

        Method toMethod();

    }


    @Getter
    @RequiredArgsConstructor
    class MethodParam {

        private final String paramName;
        private final Class<?> paramClass;
        private final boolean isContext;

    }

}

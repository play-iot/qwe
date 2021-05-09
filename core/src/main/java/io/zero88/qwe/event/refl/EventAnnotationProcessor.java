package io.zero88.qwe.event.refl;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;

import lombok.NonNull;

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

}

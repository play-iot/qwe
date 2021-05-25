package io.zero88.qwe.event.refl;

import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.UnsupportedException;

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
        return new SimpleAnnotationProcessor(ignorePackages);
    }

    /**
     * Lookup method in listener by given action
     *
     * @param listenerClass event listener class
     * @param action        event action
     * @return method metadata
     * @throws UnsupportedException if not found action in listener
     * @throws ImplementationError  if event action is bind more than one method
     * @see MethodMeta
     */
    MethodMeta lookup(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action);

}

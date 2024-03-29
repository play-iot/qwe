package cloud.playio.qwe.eventbus.refl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBContext;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.exceptions.ImplementationError;
import cloud.playio.qwe.exceptions.UnsupportedException;

import lombok.NonNull;

/**
 * The Event annotation processor
 *
 * @see EBBody
 * @see EBContext
 * @see EBContract
 * @see EBParam
 */
public interface EventAnnotationProcessor {

    String[] IGNORE_PACKAGES = {
        "java", "jdk", "sun", "com.fasterxml", "com.hazelcast", "ch.qos.logback", "org.apache", "org.junit",
        "org.gradle", "io.vertx", "io.reactivex", "io.netty"
    };

    EventAnnotationProcessor DEFAULT = create();

    static EventAnnotationProcessor create() {
        return create(IGNORE_PACKAGES);
    }

    static EventAnnotationProcessor create(String[] ignorePackages) {
        return create(ignorePackages, Arrays.asList(EBBody.class, EBParam.class, EBContext.class));
    }

    static EventAnnotationProcessor create(String[] ignorePackages,
                                           Collection<Class<? extends Annotation>> supportedParamAnnotations) {
        return new SimpleAnnotationProcessor(ignorePackages, Optional.ofNullable(supportedParamAnnotations)
                                                                     .map(HashSet::new)
                                                                     .orElseGet(HashSet::new));
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

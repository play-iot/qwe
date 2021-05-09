package io.zero88.qwe.event;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.ScanResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

public interface EventAnnotationProcessor {

    String[] IGNORE_PACKAGES = {
        "java", "jdk", "sun", "com.fasterxml", "ch.qos.logback", "org.apache", "org.junit", "org.gradle", "io.vertx",
        "io.reactivex", "io.netty"
    };

    static EventAnnotationProcessor create() {
        return create(new ClassGraph().enableClassInfo()
                                      .enableAnnotationInfo()
                                      .enableMethodInfo()
                                      .rejectPackages(IGNORE_PACKAGES)
                                      .scan());
    }

    static EventAnnotationProcessor create(ScanResult scanResult) {
        return new EventAnnotationProcessorImpl(scanResult);
    }

    MethodInfo find(@NonNull Class<? extends EventListener> listenerClass, @NonNull EventAction action);

    Object[] extract(@NonNull ObjectMapper mapper, @NonNull EventMessage message, MethodParameterInfo[] parameters);

    void close();

}

package io.zero88.qwe.auth;

import java.lang.reflect.Method;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import lombok.NonNull;

public interface AuthAnnotationProcessor {

    static AuthAnnotationProcessor create() {
        return new AuthAnnotationProcessorImpl();
    }

    @NotNull List<AuthReqDefinition> lookup(@NonNull Method method);

}

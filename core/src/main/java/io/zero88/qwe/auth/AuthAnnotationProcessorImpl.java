package io.zero88.qwe.auth;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.utils.Strings;

import lombok.NonNull;

public class AuthAnnotationProcessorImpl implements AuthAnnotationProcessor {

    @Override
    public @NotNull List<AuthReqDefinition> lookup(@NonNull Method method) {
        final Class<?> cls = method.getDeclaringClass();
        List<AuthReqDefinition> list = Optional.ofNullable(getAuthZ(method))
                                               .orElseGet(() -> Optional.ofNullable(getAuthZ(cls))
                                                                        .orElseGet(() -> getAuthZ(cls.getPackage())));
        if (list != null) {
            return list;
        }
        if (hasAuthN(method) || hasAuthN(cls) || hasAuthN(cls.getPackage())) {
            return Collections.singletonList(new AuthReqDefinition());
        }
        return new ArrayList<>();
    }

    protected boolean hasAuthN(AnnotatedElement element) {
        return element.getAnnotation(AuthN.class) != null;
    }

    protected List<AuthReqDefinition> getAuthZ(AnnotatedElement element) {
        AuthZ[] authZs = element.getAnnotationsByType(AuthZ.class);
        if (authZs == null || authZs.length == 0) {
            return null;
        }
        return Arrays.stream(authZs)
                     .map(authZ -> new AuthReqDefinition().setLoginRequired(true)
                                                          .setAllowRoles(collect(authZ.role()))
                                                          .setAllowPerms(collect(authZ.perm()))
                                                          .setAllowGroups(collect(authZ.group()))
                                                          .setCustomAccessRule(authZ.access()))
                     .collect(Collectors.toList());
    }

    private List<String> collect(String[] items) {
        return Arrays.stream(items).filter(Strings::isNotBlank).collect(Collectors.toList());
    }

}

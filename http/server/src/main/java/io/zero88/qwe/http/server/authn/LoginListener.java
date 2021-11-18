package io.zero88.qwe.http.server.authn;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventHttpService;
import io.zero88.qwe.http.EventMethodDefinition;

public interface LoginListener extends EventHttpService {

    static @Nullable LoginListener create(Class<Object> listenerClass) {
        if (listenerClass != null && ReflectionClass.assertDataType(listenerClass, LoginListener.class)) {
            return (LoginListener) ReflectionClass.createObject(listenerClass);
        }
        return null;
    }

    @Override
    default Set<EventMethodDefinition> definitions() {
        return Collections.singleton(EventMethodDefinition.create("/login",
                                                                  ActionMethodMapping.create(EventAction.parse("LOGIN"),
                                                                                             HttpMethod.POST)));
    }

}

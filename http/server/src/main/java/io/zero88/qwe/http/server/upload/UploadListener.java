package io.zero88.qwe.http.server.upload;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventHttpService;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.HttpSystem.UploadSystem;

import lombok.NonNull;

/**
 * Upload listener to handle uploaded file (update database, transfer to another host)
 *
 * @since 1.0.0
 */
public interface UploadListener extends EventHttpService, UploadSystem {

    static @Nullable UploadListener create(Class<Object> listenerClass) {
        if (listenerClass == null) {
            return null;
        }
        if (UploadListener.class.equals(listenerClass)) {
            return new LoggerUploadListener();
        }
        if (ReflectionClass.assertDataType(listenerClass, UploadListener.class)) {
            return (UploadListener) ReflectionClass.createObject(listenerClass);
        }
        return null;
    }

    @Override
    @NonNull
    default String function() {
        return UploadSystem.super.function();
    }

    /**
     * Max upload size in Megabytes
     *
     * @return the max upload size or {@code <= 0} if using configuration
     */
    default long maxUploadSize() {
        return -1;
    }

    /**
     * File upload predicate
     *
     * @return file upload predicate
     * @see FileUploadPredicate
     */
    default FileUploadPredicate predicate() {
        return FileUploadPredicate.acceptAll();
    }

    @Override
    default Set<EventMethodDefinition> definitions() {
        return Collections.singleton(
            EventMethodDefinition.create("/", ActionMethodMapping.create(EventAction.CREATE, HttpMethod.POST)));
    }

}

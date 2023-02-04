package cloud.playio.qwe.http.server.upload;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.http.HttpMethod;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.http.ActionMethodMapping;
import cloud.playio.qwe.http.EventHttpService;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.http.server.HttpSystem.UploadSystem;

import lombok.NonNull;

/**
 * Upload listener to handle uploaded file (update database, transfer to another host)
 *
 * @since 1.0.0
 */
public interface UploadListener extends EventHttpService, UploadSystem {

    static @Nullable UploadListener create(String listenerClass) {
        if (listenerClass == null) {
            return null;
        }
        if (UploadListener.class.getName().equals(listenerClass)) {
            return new LoggerUploadListener();
        }
        Class<?> cls = ReflectionClass.findClass(listenerClass);
        if (ReflectionClass.assertDataType(cls, UploadListener.class)) {
            return ReflectionClass.createObject(listenerClass);
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

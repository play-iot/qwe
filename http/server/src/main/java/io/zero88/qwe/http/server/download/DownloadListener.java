package io.zero88.qwe.http.server.download;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventHttpService;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.HttpSystem.DownloadSystem;

import lombok.NonNull;

public interface DownloadListener extends EventHttpService, DownloadSystem {

    static @Nullable DownloadListener create(Class<Object> listenerClass) {
        if (listenerClass == null) {
            return null;
        }
        if (DownloadListener.class.equals(listenerClass)) {
            return new LocalDownloadFileListener();
        }
        if (ReflectionClass.assertDataType(listenerClass, DownloadListener.class)) {
            return (DownloadListener) ReflectionClass.createObject(listenerClass);
        }
        return null;
    }

    @Override
    @NonNull
    default String function() {
        return DownloadSystem.super.function();
    }

    @Override
    default Set<EventMethodDefinition> definitions() {
        return Collections.singleton(EventMethodDefinition.create("/", "file", ActionMethodMapping.create(
            EventAction.parse("DOWNLOAD"), HttpMethod.GET)));
    }

    DownloadListener setup(Path rootDownloadDir);

    Path getDownloadDir();

    default StaticHandler getStaticHandler() {
        return StaticHandler.create()
                            .setEnableRangeSupport(true)
                            .setSendVaryHeader(true)
                            .setFilesReadOnly(false)
                            .setAllowRootFileSystemAccess(true)
                            .setIncludeHidden(false)
                            .setWebRoot(getDownloadDir().toString());
    }

}

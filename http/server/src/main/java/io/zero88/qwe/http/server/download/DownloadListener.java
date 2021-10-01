package io.zero88.qwe.http.server.download;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventHttpService;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.HttpSystem.DownloadSystem;

import lombok.NonNull;

public interface DownloadListener extends EventHttpService, DownloadSystem {

    static @Nullable DownloadListener create(String listenerClass) {
        if (Strings.isBlank(listenerClass)) {
            return null;
        }
        if (DownloadListener.class.getName().equals(listenerClass)) {
            return new LocalDownloadFileListener();
        }
        Class<?> cls = ReflectionClass.findClass(listenerClass);
        if (ReflectionClass.assertDataType(cls, DownloadListener.class)) {
            return ReflectionClass.createObject(listenerClass);
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

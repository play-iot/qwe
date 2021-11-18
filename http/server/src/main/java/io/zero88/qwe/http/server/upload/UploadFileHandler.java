package io.zero88.qwe.http.server.upload;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.http.server.HttpSystem.UploadSystem;
import io.zero88.qwe.http.server.handler.EventBusProxyDispatcher.EventMessageResponseDispatcher;
import io.zero88.qwe.http.server.handler.HttpEBDispatcher;

/**
 * Upload file handler
 *
 * @since 1.0.0
 */
public interface UploadFileHandler extends EventMessageResponseDispatcher, UploadSystem {

    static UploadFileHandler create(String handlerClass) {
        if (Strings.isBlank(handlerClass) || UploadFileHandler.class.getName().equals(handlerClass)) {
            return new UploadFileHandlerImpl();
        }
        return Objects.requireNonNull(ReflectionClass.createObject(handlerClass));
    }

    UploadFileHandler setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition,
                            FileUploadPredicate predicate);

}

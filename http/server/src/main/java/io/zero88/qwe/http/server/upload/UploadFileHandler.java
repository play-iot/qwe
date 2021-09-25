package io.zero88.qwe.http.server.upload;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.server.HttpSystem.UploadSystem;
import io.zero88.qwe.http.server.handler.EventBusProxyDispatcher;
import io.zero88.qwe.http.server.handler.HttpEBDispatcher;
import io.zero88.qwe.http.server.handler.ResponseEventInterceptor;
import io.zero88.qwe.http.server.handler.ResponseInterceptor;

/**
 * Upload file handler
 *
 * @since 1.0.0
 */
public interface UploadFileHandler extends EventBusProxyDispatcher<EventMessage>, UploadSystem {

    static UploadFileHandler create(String handlerClass) {
        if (Strings.isBlank(handlerClass) || UploadFileHandler.class.getName().equals(handlerClass)) {
            return new UploadFileHandlerImpl();
        }
        return Objects.requireNonNull(ReflectionClass.createObject(handlerClass));
    }

    UploadFileHandler setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition, FileUploadPredicate predicate);

    @Override
    default ResponseInterceptor<EventMessage> responseInterceptor() {
        return new ResponseEventInterceptor();
    }

    @Override
    default EventMessage convert(EventMessage resp) {
        return resp;
    }

}

package io.zero88.qwe.http.client;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.http.client.handler.WebSocketClientDispatcher;
import io.zero88.qwe.http.client.handler.WebSocketClientErrorHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public final class WebSocketHandlersConfig {

    private Class<? extends WebSocketClientErrorHandler> errorHandlerCls = WebSocketClientErrorHandler.class;
    private Class<? extends WebSocketClientDispatcher> dispatcherCls = WebSocketClientDispatcher.class;

    @JsonCreator
    WebSocketHandlersConfig(@JsonProperty("errorHandlerCls") String errorHandlerCls,
                            @JsonProperty("dispatcherCls") String dispatcherCls) {
        this.errorHandlerCls = Strings.isBlank(errorHandlerCls)
                               ? WebSocketClientErrorHandler.class
                               : ReflectionClass.findClass(errorHandlerCls);
        this.dispatcherCls = Strings.isBlank(dispatcherCls)
                             ? WebSocketClientDispatcher.class
                             : ReflectionClass.findClass(dispatcherCls);
    }

}

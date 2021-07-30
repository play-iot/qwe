package io.zero88.qwe.http.client;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.http.client.handler.HttpResponseBinaryHandler;
import io.zero88.qwe.http.client.handler.HttpClientJsonResponseHandler;

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
public final class HttpHandlersConfig {

    private Class<? extends HttpClientJsonResponseHandler> respTextHandlerCls = HttpClientJsonResponseHandler.class;
    private Class<? extends HttpResponseBinaryHandler> respBinaryHandlerCls = HttpResponseBinaryHandler.class;

    @JsonCreator
    HttpHandlersConfig(@JsonProperty("respTextHandlerCls") String respTextHandlerCls,
                       @JsonProperty("respBinaryHandlerCls") String respBinaryHandlerCls) {
        this.respTextHandlerCls = Strings.isBlank(respTextHandlerCls)
                                  ? HttpClientJsonResponseHandler.class
                                  : ReflectionClass.findClass(respTextHandlerCls);
        this.respBinaryHandlerCls = Strings.isBlank(respBinaryHandlerCls)
                                    ? HttpResponseBinaryHandler.class
                                    : ReflectionClass.findClass(respBinaryHandlerCls);
    }

}

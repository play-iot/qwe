package io.zero88.qwe.http.server.authn;

import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.http.server.HttpSystem.AuthNSystem;
import io.zero88.qwe.http.server.handler.AuthInterceptor;
import io.zero88.qwe.http.server.handler.EventBusProxyDispatcher.EventMessageResponseDispatcher;
import io.zero88.qwe.http.server.handler.RequestDataInterceptor;
import io.zero88.qwe.http.server.handler.RequestInterceptor;

public interface AuthNDispatcher extends EventMessageResponseDispatcher, AuthNSystem {

    default RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.createSlim();
    }

    default AuthInterceptor authInterceptor() {
        return AuthInterceptor.create(ReqAuthDefinition.noAuth());
    }

}

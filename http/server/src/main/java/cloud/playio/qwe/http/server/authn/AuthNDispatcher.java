package cloud.playio.qwe.http.server.authn;

import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.http.server.HttpSystem.AuthNSystem;
import cloud.playio.qwe.http.server.handler.AuthInterceptor;
import cloud.playio.qwe.http.server.handler.EventBusProxyDispatcher.EventMessageResponseDispatcher;
import cloud.playio.qwe.http.server.handler.RequestDataInterceptor;
import cloud.playio.qwe.http.server.handler.RequestInterceptor;

public interface AuthNDispatcher extends EventMessageResponseDispatcher, AuthNSystem {

    default RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.createSlim();
    }

    default AuthInterceptor authInterceptor() {
        return AuthInterceptor.create(ReqAuthDefinition.noAuth());
    }

}

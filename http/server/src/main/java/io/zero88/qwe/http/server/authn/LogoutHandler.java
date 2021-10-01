package io.zero88.qwe.http.server.authn;

import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.exceptions.SecurityException.AuthenticationException;
import io.zero88.qwe.http.server.handler.RequestDataInterceptor;
import io.zero88.qwe.http.server.handler.RequestInterceptor;

public interface LogoutHandler extends AuthNDispatcher {

    default RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.createSlim().onBefore(ctx -> {
            if (ctx.user() == null) {
                throw new AuthenticationException("Required login");
            }
            return ctx;
        });
    }

}

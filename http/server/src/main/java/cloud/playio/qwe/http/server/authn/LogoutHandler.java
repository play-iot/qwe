package cloud.playio.qwe.http.server.authn;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.exceptions.SecurityException.AuthenticationException;
import cloud.playio.qwe.http.server.handler.RequestDataInterceptor;
import cloud.playio.qwe.http.server.handler.RequestInterceptor;

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

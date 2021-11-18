package io.zero88.qwe.http.server.handler;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.exceptions.SecurityException.AuthenticationException;

/**
 * The dummy AuthN/AuthZ interceptor verifies required login if any and throw exception if required authorization
 */
public final class AuthInterceptorDummy implements AuthInterceptor {

    private ReqAuthDefinition authDefinition;

    @Override
    public AuthInterceptor setup(ReqAuthDefinition authDefinition) {
        this.authDefinition = Optional.ofNullable(authDefinition).orElseGet(ReqAuthDefinition::noAuth);
        return this;
    }

    @Override
    public Future<UserInfo> filter(RoutingContext context) {
        if (context.user() == null) {
            if (authDefinition.isLoginRequired()) {
                return Future.failedFuture(new AuthenticationException("Required login or bad credential"));
            }
            return Future.succeededFuture(null);
        }
        if (authDefinition.isAuthzRequired()) {
            return Future.failedFuture(new SecurityException("Unknown authorization"));
        }
        return Future.succeededFuture(userConverter().from(context.user()));
    }

}

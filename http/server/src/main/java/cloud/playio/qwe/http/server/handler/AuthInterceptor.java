package cloud.playio.qwe.http.server.handler;

import java.util.Optional;

import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.auth.UserInfo;

/**
 * The AuthN/AuthZ interceptor SPI.
 * <p>
 * To implement security feature, you need to implement your own and put your class in classpath resource {@code
 * META-INF/services/cloud.playio.qwe.http.server.handler.AuthInterceptor}
 */
public interface AuthInterceptor extends RequestInterceptor<UserInfo> {

    static AuthInterceptor create(ReqAuthDefinition authDefinition) {
        return Optional.ofNullable(AuthInterceptorLoader.getInstance().get())
                       .orElseGet(AuthInterceptorDummy::new)
                       .setup(authDefinition);
    }

    AuthInterceptor setup(ReqAuthDefinition authDefinition);

    default UserConverter userConverter() {
        return UserConverter.create();
    }

}

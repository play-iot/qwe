package io.zero88.qwe.http.server.rest.handler;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.ext.auth.User;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

/**
 * Represents for pushing data via {@code EventBus} then listen {@code reply message}. After receiving {@code reply
 * message}, redirect it to {@code next Context handler}
 *
 * @see EventMessageResponseHandler
 */
public interface RestEventApiDispatcher extends RestEventRequestDispatcher {

    static RestEventApiDispatcher create(Class<RestEventApiDispatcher> cls) {
        if (cls == null) {
            return ReflectionClass.createObject(RestEventApiDispatcherImpl.class);
        }
        return Objects.requireNonNull(ReflectionClass.createObject(cls), "Unable create REST dispatcher");
    }

    RestEventApiDispatcher setup(DeliveryEvent deliveryEvent, String sharedKey);

    //TODO move to interceptor
    static UserInfo convertUser(User user) {
        return user == null ? null : UserInfo.create(user.get("username"), user.attributes());
    }

}

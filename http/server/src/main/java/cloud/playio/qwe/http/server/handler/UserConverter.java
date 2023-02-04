package cloud.playio.qwe.http.server.handler;

import java.util.Optional;
import java.util.stream.Stream;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import cloud.playio.qwe.QWEConverter;
import cloud.playio.qwe.auth.UserInfo;

public interface UserConverter extends QWEConverter<User, UserInfo> {

    static UserConverter create() {
        return new DefaultUserConverter();
    }

    @Override
    default Class<User> fromClass() {
        return User.class;
    }

    @Override
    default Class<UserInfo> toClass() {
        return UserInfo.class;
    }

    class DefaultUserConverter implements UserConverter {

        @Override
        public UserInfo from(User user) {
            if (user == null) {
                return null;
            }
            return UserInfo.create(Stream.of("username", "access_token", "id", "identifier", "user_name", "user")
                                         .map(s -> (String) user.get(s))
                                         .findFirst()
                                         .orElseThrow(() -> new IllegalArgumentException("Unknown user identifier")),
                                   new JsonObject().put("principal", user.principal())
                                                   .put("attributes", user.attributes()));
        }

        @Override
        public User to(UserInfo userInfo) {
            if (userInfo == null) {
                return null;
            }
            return User.create(Optional.ofNullable((JsonObject) userInfo.get("principal")).orElseGet(JsonObject::new),
                               (JsonObject) userInfo.get("attributes"));
        }

    }

}

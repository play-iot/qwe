package cloud.playio.qwe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.AuthenticationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.auth.ReqAuthZDefinition;
import cloud.playio.qwe.auth.SecurityFilter;
import cloud.playio.qwe.auth.UserInfo;
import cloud.playio.qwe.exceptions.SecurityException.InsufficientPermissionError;

import lombok.NonNull;

public class MemorySecurityStore implements SecurityFilter {

    public static MemorySecurityStore defaultUsers() {
        return new MemorySecurityStore().put(UserInfo.create("zero88", new JsonObject()))
                                        .put(UserInfo.create("playio", new JsonObject()))
                                        .put(UserInfo.create("qwe", new JsonObject()));
    }

    private final Map<String, UserInfo> userDB = new HashMap<>();

    public MemorySecurityStore put(UserInfo userInfo) {
        userDB.put(userInfo.identifier(), userInfo);
        return this;
    }

    @Override
    public @NotNull Future<Void> check(@NonNull SharedDataLocalProxy sharedData, @Nullable UserInfo userInfo,
                                       @NonNull ReqAuthDefinition reqDefinition) {
        if (userInfo == null) {
            if (reqDefinition.isLoginRequired()) {
                return Future.failedFuture(new AuthenticationException("Required login"));
            }
            return Future.succeededFuture();
        }
        final UserInfo dbUser = userDB.get(userInfo.identifier());
        if (dbUser == null) {
            return Future.failedFuture(
                new AuthenticationException("User[" + userInfo.identifier() + "] is non-existent"));
        }
        if (!reqDefinition.isAuthzRequired()) {
            return Future.succeededFuture();
        }
        if (reqDefinition.getAuthz().stream().anyMatch(authz -> validate(dbUser, authz))) {
            return Future.succeededFuture();
        }
        return Future.failedFuture(
            new InsufficientPermissionError("User[" + userInfo.identifier() + "] is not authorized"));
    }

    protected boolean validate(UserInfo userInfo, ReqAuthZDefinition authzDefinition) {
        return validate(userInfo.get("role"), authzDefinition.getAllowRoles()) &&
               validate(userInfo.get("perm"), authzDefinition.getAllowPerms()) &&
               validate(userInfo.get("group"), authzDefinition.getAllowGroups());
    }

    protected boolean validate(Object actual, List<String> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return true;
        }
        if (actual instanceof String) {
            return definitions.contains(actual);
        }
        if (actual instanceof Collection || actual instanceof JsonArray) {
            for (Object o : (Iterable<?>) actual) {
                if (definitions.contains(o.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

}

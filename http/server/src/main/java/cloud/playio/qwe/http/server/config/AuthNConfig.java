package cloud.playio.qwe.http.server.config;

import java.util.Set;

import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.AuthNSystem;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class AuthNConfig extends AbstractRouterConfig implements AuthNSystem {

    public static final String NAME = "__authn__";
    private Set<String> loginListenerClasses;
    private String logoutPath = "/logout";
    private String logoutListenerClass;
    private String recoveryPath = "/recovery";
    private String recoveryListenerClass;
    private String signupPath = "/signup";
    private String signupListenerClass;

    public AuthNConfig() {
        super(NAME, HttpServerConfig.class);
        this.setEnabled(false);
    }

    @Override
    protected @NonNull String defaultPath() {
        return "/a";
    }

}

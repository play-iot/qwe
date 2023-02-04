package cloud.playio.qwe.eventbus.mock;

import java.util.Optional;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.auth.AuthN;
import cloud.playio.qwe.auth.AuthZ;
import cloud.playio.qwe.auth.SecurityFilter;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.eventbus.EventListenerExecutor;

import lombok.RequiredArgsConstructor;

@AuthN
@RequiredArgsConstructor
public class MockAuthListener implements EventListener {

    private final SecurityFilter securityFilter;

    public MockAuthListener() {
        this(null);
    }

    @EBContract(action = "CHECK")
    public String check() {
        return "success";
    }

    @AuthZ(role = "admin")
    @EBContract(action = "CREATE")
    public String create() {
        return "success";
    }

    @AuthZ(perm = "query")
    @EBContract(action = "QUERY")
    public String query() {
        return "success";
    }

    @Override
    public EventListenerExecutor executor(SharedDataLocalProxy sharedData) {
        return Optional.ofNullable(securityFilter)
                       .map(sf -> EventListenerExecutor.create(this, sharedData, sf))
                       .orElseGet(() -> EventListener.super.executor(sharedData));
    }

}

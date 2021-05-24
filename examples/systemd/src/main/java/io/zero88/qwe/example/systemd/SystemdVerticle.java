package io.zero88.qwe.example.systemd;

import io.zero88.qwe.example.CounterProducer;

public final class SystemdVerticle extends CounterProducer {

    @Override
    public String appName() {
        return "Systemd Example";
    }

}

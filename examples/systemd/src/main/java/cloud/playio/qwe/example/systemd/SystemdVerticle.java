package cloud.playio.qwe.example.systemd;

import cloud.playio.qwe.example.CounterProducer;

public final class SystemdVerticle extends CounterProducer {

    @Override
    public String appName() {
        return "systemd-example";
    }

}

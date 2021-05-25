package io.zero88.qwe.example.docker;

import io.zero88.qwe.example.CounterProducer;

public final class DockerVerticle extends CounterProducer {

    @Override
    public String appName() {
        return "Docker Example";
    }

}

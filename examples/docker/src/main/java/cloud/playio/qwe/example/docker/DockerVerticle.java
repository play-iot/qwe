package cloud.playio.qwe.example.docker;

import cloud.playio.qwe.example.CounterProducer;

public final class DockerVerticle extends CounterProducer {

    @Override
    public String appName() {
        return "docker-example";
    }

}

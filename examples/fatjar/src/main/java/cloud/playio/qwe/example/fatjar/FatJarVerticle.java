package cloud.playio.qwe.example.fatjar;

import cloud.playio.qwe.example.CounterConsumer;

public final class FatJarVerticle extends CounterConsumer {

    @Override
    public String appName() {
        return "fat-jar-example";
    }

}

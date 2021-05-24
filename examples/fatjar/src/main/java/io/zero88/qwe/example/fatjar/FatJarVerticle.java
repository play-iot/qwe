package io.zero88.qwe.example.fatjar;

import io.zero88.qwe.example.CounterConsumer;

public final class FatJarVerticle extends CounterConsumer {

    @Override
    public String appName() {
        return "Fat jar example";
    }

}

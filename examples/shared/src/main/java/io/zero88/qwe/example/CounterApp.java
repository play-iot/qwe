package io.zero88.qwe.example;

public interface CounterApp {

    /**
     * Application name
     *
     * @return app name
     */
    String appName();

    /**
     * An EventBus address for publish/subscribe counter
     *
     * @return counter address
     */
    default String address() {
        return "qwe.example.counter";
    }

}

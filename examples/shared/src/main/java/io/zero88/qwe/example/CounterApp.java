package io.zero88.qwe.example;

public interface CounterApp {

    /**
     * An EventBus address for publish/subscribe counter
     *
     * @return counter address
     */
    default String address() {
        return "qwe.example.counter";
    }

}

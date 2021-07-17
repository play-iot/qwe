package io.zero88.qwe.example;

import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EBParam;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventListener;

public abstract class CounterConsumer extends ApplicationVerticle implements CounterApp {

    @Override
    public void onStart() {
        EventBusClient.create(sharedData()).register(address(), false, new CounterListener());
    }

    public static class CounterListener implements EventListener {

        @EBContract(action = "NOTIFY")
        public void receive(@EBParam("count") int counter, @EBParam("app") String appName) {
            logger().info("*********** Receive counter from [{}] value [{}]", appName, counter);
        }

    }

}

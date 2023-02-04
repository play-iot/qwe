package cloud.playio.qwe.example;

import cloud.playio.qwe.ApplicationVerticle;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventListener;

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

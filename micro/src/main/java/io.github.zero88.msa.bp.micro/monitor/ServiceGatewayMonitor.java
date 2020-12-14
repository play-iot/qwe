package io.github.zero88.msa.bp.micro.monitor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ServiceGatewayMonitor extends Handler<Message<Object>> {

    static <T extends ServiceGatewayMonitor> T create(@NonNull Vertx vertx,
                                                      @NonNull ServiceDiscoveryController controller, String sharedKey,
                                                      String className, @NonNull Class<T> fallback) {
        Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(Vertx.class, vertx);
        inputs.put(ServiceDiscoveryController.class, controller);
        inputs.put(String.class, Strings.requireNotBlank(sharedKey));
        if (fallback.getName().equals(className) || Strings.isBlank(className)) {
            return ReflectionClass.createObject(fallback, inputs);
        }
        T monitor = ReflectionClass.createObject(className, inputs);
        return Objects.isNull(monitor) ? ReflectionClass.createObject(fallback, inputs) : monitor;
    }

    @NonNull Vertx getVertx();

    @NonNull ServiceDiscoveryController getController();

    @NonNull String getSharedKey();

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class AbstractServiceGatewayMonitor implements ServiceGatewayMonitor {

        protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        @NonNull
        private final Vertx vertx;
        @NonNull
        private final ServiceDiscoveryController controller;
        @NonNull
        private final String sharedKey;

    }

}

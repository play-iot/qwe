package io.github.zero88.qwe.micro.monitor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.micro.ServiceDiscoveryController;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface ServiceGatewayMonitor extends Handler<Message<Object>>, HasSharedData {

    static <T extends ServiceGatewayMonitor> T create(@NonNull SharedDataLocalProxy proxy,
                                                      @NonNull ServiceDiscoveryController controller, String className,
                                                      @NonNull Class<T> fallback) {
        Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(SharedDataLocalProxy.class, proxy);
        inputs.put(ServiceDiscoveryController.class, controller);
        if (fallback.getName().equals(className) || Strings.isBlank(className)) {
            return ReflectionClass.createObject(fallback, inputs);
        }
        T monitor = ReflectionClass.createObject(className, inputs);
        return Objects.isNull(monitor) ? ReflectionClass.createObject(fallback, inputs) : monitor;
    }

    @NonNull ServiceDiscoveryController getController();

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class AbstractServiceGatewayMonitor implements ServiceGatewayMonitor {

        protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        @NonNull
        @Accessors(fluent = true)
        private final SharedDataLocalProxy sharedData;
        @NonNull
        private final ServiceDiscoveryController controller;

    }

}

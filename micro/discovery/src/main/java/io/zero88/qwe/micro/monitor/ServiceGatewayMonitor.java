package io.zero88.qwe.micro.monitor;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.repl.Arguments;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.micro.ServiceDiscoveryApi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface ServiceGatewayMonitor extends Handler<Message<Object>>, HasSharedData {

    static <T extends ServiceGatewayMonitor> T create(@NonNull SharedDataLocalProxy proxy, ServiceDiscoveryApi wrapper,
                                                      String className, @NonNull Class<T> fallback) {
        final Arguments args = new Arguments().put(SharedDataLocalProxy.class, proxy)
                                              .put(ServiceDiscoveryApi.class, wrapper);
        if (fallback.getName().equals(className) || Strings.isBlank(className)) {
            return ReflectionClass.createObject(fallback, args);
        }
        T monitor = ReflectionClass.createObject(className, args);
        return Objects.isNull(monitor) ? ReflectionClass.createObject(fallback, args) : monitor;
    }

    @NonNull ServiceDiscoveryApi getDiscovery();

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class AbstractServiceGatewayMonitor<T> implements ServiceGatewayMonitor {

        protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        @NonNull
        @Accessors(fluent = true)
        private final SharedDataLocalProxy sharedData;
        @NonNull
        private final ServiceDiscoveryApi discovery;

        abstract String function();

        Message<Object> trace(Message<Object> message) {
            if (logger.isTraceEnabled()) {
                logger.trace("{}::Receive message [{}] - Headers[{}] - Body[{}]", function(), message.address(),
                             message.headers(), message.body());
            }
            return message;
        }

        @Override
        public final void handle(Message<Object> message) {
            process(parse(trace(message)));
        }

        protected abstract T parse(Message<Object> message);

        protected abstract void process(T record);

    }

}

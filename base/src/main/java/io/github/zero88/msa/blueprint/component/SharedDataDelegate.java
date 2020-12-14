package io.github.zero88.msa.blueprint.component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.msa.blueprint.event.EventbusClient;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * SharedData delegate
 *
 * @param <T> Type of instance that inherited from {@link SharedDataDelegate}
 */
public interface SharedDataDelegate<T extends SharedDataDelegate> {

    Logger LOGGER = LoggerFactory.getLogger(SharedDataDelegate.class);

    /**
     * Data key for EventBus controller
     *
     * @see EventbusClient
     */
    String SHARED_EVENTBUS = "EVENTBUS_CLIENT";
    String SHARED_DATADIR = "DATADIR";

    @SuppressWarnings("unchecked")
    static <D> D getSharedDataValue(Function<String, Object> sharedDataFunc, String dataKey) {
        try {
            return (D) sharedDataFunc.apply(dataKey);
        } catch (ClassCastException e) {
            LOGGER.warn("Data value Type is not matching with expected data key {}", e, dataKey);
            return null;
        }
    }

    static <D> D getLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey) {
        LOGGER.trace("GET | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        return SharedDataDelegate.getSharedDataValue(
            k -> vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).get(k), dataKey);
    }

    static <D> D addLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey, D data) {
        LOGGER.trace("ADD | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).put(Strings.requireNotBlank(dataKey), data);
        return data;
    }

    static <D> D removeLocalDataValue(@NonNull Vertx vertx, String sharedKey, String dataKey) {
        LOGGER.trace("POP | Shared Key: \"{}\" | Shared Data Key: \"{}\"", sharedKey, dataKey);
        return SharedDataDelegate.getSharedDataValue(
            k -> vertx.sharedData().getLocalMap(Strings.requireNotBlank(sharedKey)).remove(k), dataKey);
    }

    static EventbusClient getEventController(@NonNull Vertx vertx, String sharedKey) {
        final EventbusClient eventbusClient = getLocalDataValue(vertx, sharedKey, SHARED_EVENTBUS);
        if (Objects.nonNull(eventbusClient)) {
            return eventbusClient;
        }
        synchronized (EventbusClient.class) {
            final EventbusClient controller = new DefaultEventClient(vertx);
            addLocalDataValue(vertx, sharedKey, SHARED_EVENTBUS, controller);
            return controller;
        }
    }

    static Path getDataDir(@NonNull Vertx vertx, String sharedKey) {
        return Paths.get((String) getLocalDataValue(vertx, sharedKey, SHARED_DATADIR));
    }

    /**
     * Get shared data value by data key
     *
     * @param dataKey Given data key
     * @param <D>     Type of data value
     * @return Data value. It may be {@code null} if no data value by key or data value type doesn't match type with
     *     expected value
     */
    <D> D getSharedDataValue(String dataKey);

    /**
     * System will register it automatically. You don't need call it directly
     *
     * @param sharedDataFunc Given shared data function from {@code Vertx Verticle}
     * @return a reference to this, so the API can be used fluently
     */
    T registerSharedData(@NonNull Function<String, Object> sharedDataFunc);

    @Getter
    @NoArgsConstructor
    abstract class AbstractSharedDataDelegate<T extends SharedDataDelegate> implements SharedDataDelegate<T> {

        protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        private Vertx vertx;
        private String sharedKey;
        private Function<String, Object> sharedDataFunc;

        public AbstractSharedDataDelegate(@NonNull Vertx vertx) {
            this.vertx = vertx;
        }

        @Override
        public final <D> D getSharedDataValue(String dataKey) {
            if (Objects.nonNull(vertx)) {
                return SharedDataDelegate.getLocalDataValue(vertx, sharedKey, dataKey);
            }
            return SharedDataDelegate.getSharedDataValue(sharedDataFunc, dataKey);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final T registerSharedData(@NonNull Function<String, Object> sharedDataFunc) {
            this.sharedDataFunc = sharedDataFunc;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        protected final T registerSharedKey(String sharedKey) {
            this.sharedKey = Strings.requireNotBlank(sharedKey, "Shared key cannot be empty");
            return (T) this;
        }

    }

}

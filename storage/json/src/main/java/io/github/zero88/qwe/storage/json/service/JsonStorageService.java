package io.github.zero88.qwe.storage.json.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.zero88.qwe.component.HasSharedData;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.file.ReadableFile;
import io.github.zero88.qwe.file.converter.BufferConverter;
import io.github.zero88.qwe.storage.json.StorageConfig;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JsonStorageService implements EventListener, HasSharedData {

    public static <T extends JsonStorageService> T create(@NonNull SharedDataLocalProxy sharedData,
                                                          @NonNull StorageConfig config, @NonNull Class<T> clazz) {
        final Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(SharedDataLocalProxy.class, sharedData);
        inputs.put(StorageConfig.class, config);
        return ReflectionClass.createObject(clazz, inputs);
    }

    private final SharedDataLocalProxy sharedData;
    private final StorageConfig config;

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return Arrays.asList(EventAction.CREATE_OR_UPDATE, EventAction.REMOVE, EventAction.parse("QUERY"),
                             EventAction.parse("HAS"));
    }

    @EventContractor(action = "CREATE_OR_UPDATE", returnType = Single.class)
    public Single<JsonObject> createOrUpdate(RequestData requestData) {
        final JsonInput ji = parse(requestData);
        final JsonPointer pointer = ji.pointer();
        return this.loadJson(ji)
                   .map(json -> Objects.requireNonNull(pointer.writeJson(json, ji.getDataToInsert(), true),
                                                       "Unable to write"))
                   .flatMap(o -> writeJson(ji, o).map(ignore -> pointer.isRootPointer() ? o : pointer.queryJson(o)))
                   .map(o -> new JsonObject().put(ji.getOutputKey(), o));
    }

    @EventContractor(action = "REMOVE", returnType = Single.class)
    public Single<JsonObject> remove(RequestData requestData) {
        final JsonInput ji = parse(requestData);
        if (Objects.isNull(ji.getKeyToRemove())) {
            throw new IllegalArgumentException("Key to remove is mandatory");
        }
        final JsonPointer pointer = ji.pointer();
        return this.loadJson(ji)
                   .flatMapMaybe(json -> this.remove(pointer, ji, json)
                                             .flatMapSingleElement(o -> this.writeJson(ji, json).map(ignore -> o)))
                   .map(o -> new JsonObject().put(ji.getOutputKey(), o))
                   .switchIfEmpty(Single.just(new JsonObject()));
    }

    @EventContractor(action = "QUERY", returnType = Single.class)
    public Single<JsonObject> query(RequestData requestData) {
        final JsonInput ji = parse(requestData);
        return loadJson(ji).map(json -> new JsonObject().put(ji.getOutputKey(), ji.pointer().queryJson(json)));
    }

    @EventContractor(action = "HAS", returnType = Single.class)
    public Single<JsonObject> has(RequestData requestData) {
        final JsonInput ji = parse(requestData);
        return this.loadJson(ji)
                   .map(json -> new JsonObject().put(ji.getOutputKey(), Objects.nonNull(ji.pointer().queryJson(json))));
    }

    protected Maybe<?> remove(JsonPointer pointer, JsonInput ji, JsonObject json) {
        final String keyToRemove = ji.getKeyToRemove().toString();
        final Object o = pointer.queryJson(json);
        if (o instanceof JsonObject) {
            return Optional.ofNullable(((JsonObject) o).remove(keyToRemove))
                           .map(v -> ji.isSkipRemovedKeyInOutput() ? v : new JsonObject().put(keyToRemove, v))
                           .map(Maybe::just)
                           .orElse(Maybe.empty());
        }
        if (o instanceof JsonArray) {
            return Functions.getIfThrow(() -> Integer.parseInt(keyToRemove))
                            .flatMap(idx -> Optional.ofNullable(((JsonArray) o).remove((int) idx)))
                            .map(Maybe::just)
                            .orElse(Maybe.empty());
        }
        return Maybe.empty();
    }

    protected JsonInput parse(RequestData requestData) {
        return JsonData.from(requestData.body(), JsonInput.class);
    }

    protected Single<JsonArray> loadArray(@NonNull JsonInput ji) {
        return helper().loadArray(config.fullPath().resolve(ji.getFile()), config.getOption());
    }

    protected Single<JsonObject> loadJson(@NonNull JsonInput ji) {
        return helper().loadJson(config.fullPath().resolve(ji.getFile()), config.getOption());
    }

    protected Single<Path> writeJson(@NonNull JsonInput ji, Object data) {
        return helper().write(config.fullPath().resolve(ji.getFile()), config.getOption(),
                              JsonData.tryParse(data).toJson(), BufferConverter.JSON_OBJECT_CONVERTER);
    }

    private ReadableFile helper() {
        return ReadableFile.builder().vertx(sharedData.getVertx()).maxSize(config.getMaxSizeInMB()).build();
    }

}

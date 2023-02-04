package cloud.playio.qwe.sql.pojos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.JsonData;

import lombok.NonNull;

public interface CompositePojo<P extends JsonRecord, CP extends CompositePojo> extends JsonRecord {

    @SuppressWarnings("unchecked")
    static <M extends JsonRecord, C extends CompositePojo> C create(Object pojo, Class<M> pojoClass, Class<C> clazz) {
        return (C) ReflectionClass.createObject(clazz).wrap(pojoClass.cast(pojo));
    }

    /**
     * Defines extension pojo
     *
     * @return extension pojo
     */
    @NonNull ExtensionPojo extension();

    @NonNull CP wrap(@NonNull P pojo);

    /**
     * Wrap external properties
     *
     * @param other Map external properties
     * @return a reference to this, so the API can be used fluently
     */
    @SuppressWarnings("unchecked")
    default @NonNull CP wrap(@NonNull Map<String, JsonRecord> other) {
        extension().other.putAll(other);
        return (CP) this;
    }

    /**
     * Put external property
     *
     * @param otherKey External key
     * @param pojo     External pojo
     * @return a reference to this, so the API can be used fluently
     */
    @SuppressWarnings("unchecked")
    default @NonNull CP put(String otherKey, @NonNull JsonRecord pojo) {
        extension().other.put(Strings.requireNotBlank(otherKey), pojo);
        return (CP) this;
    }

    /**
     * Put external properties
     *
     * @param otherKey External key
     * @param pojos    list of pojo with same kind
     * @return a reference to this, so the API can be used fluently
     */
    @SuppressWarnings("unchecked")
    default @NonNull CP put(String otherKey, @NonNull List<JsonRecord> pojos) {
        extension().others.put(Strings.requireNotBlank(otherKey), pojos);
        return (CP) this;
    }

    default Object prop(String key) {
        return this.toJson().getValue(Strings.requireNotBlank(key));
    }

    /**
     * Update raw property
     *
     * @param key   Raw key
     * @param value Value
     * @return a reference to this, so the API can be used fluently
     */
    @SuppressWarnings("unchecked")
    default CP with(String key, Object value) {
        return (CP) fromJson(this.toJson().put(Strings.requireNotBlank(key), value));
    }

    @SuppressWarnings("unchecked")
    default <M extends JsonRecord> M getOther(String key) {
        return (M) extension().other.get(Strings.requireNotBlank(key));
    }

    @SuppressWarnings("unchecked")
    default <M extends JsonRecord> M safeGetOther(String key, @NonNull Class<M> clazz) {
        final JsonRecord data = extension().other.get(Strings.requireNotBlank(key));
        return clazz.isInstance(data) ? (M) data : null;
    }

    default List<JsonRecord> getOthers(String key) {
        return extension().others.get(Strings.requireNotBlank(key));
    }

    default JsonObject extensionToJson() {
        return extension().toJson();
    }

    JsonObject toJsonWithoutExt();

    final class ExtensionPojo implements JsonData {

        final @NonNull Map<String, JsonRecord> other = new HashMap<>();

        final @NonNull Map<String, List<JsonRecord>> others = new HashMap<>();

        @Override
        public JsonObject toJson() {
            JsonObject m1 = new JsonObject(
                other.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toJson())));
            JsonObject m2 = new JsonObject(others.entrySet()
                                                 .stream()
                                                 .collect(Collectors.toMap(Entry::getKey, e -> e.getValue()
                                                                                                .stream()
                                                                                                .map(JsonRecord::toJson)
                                                                                                .collect(
                                                                                                    Collectors.toList()))));
            return m1.mergeIn(m2);
        }

    }

}

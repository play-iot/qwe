package io.zero88.qwe.sql.converter;

import java.util.Objects;
import java.util.stream.Stream;

import org.jooq.Converter;

import io.vertx.core.json.JsonArray;

public abstract class ArrayConverter<T> implements Converter<Object[], JsonArray> {

    @Override
    public JsonArray from(Object[] databaseObject) {
        JsonArray jsonArray = new JsonArray();
        if (Objects.nonNull(databaseObject)) {
            Stream.of(databaseObject).map(this::parse).filter(Objects::nonNull).forEach(jsonArray::add);
        }
        return jsonArray;
    }

    @Override
    public Object[] to(JsonArray userObject) {
        return Objects.isNull(userObject) ? null : userObject.getList().toArray(new Object[] {});
    }

    @Override
    public Class<Object[]> fromType() {
        return Object[].class;
    }

    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }

    public abstract T parse(Object object);

    public abstract Class<T> itemClass();

}

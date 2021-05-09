package io.zero88.qwe.file.converter;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.CarlConverter;

import lombok.NonNull;

public interface BufferConverter<D> extends CarlConverter<Buffer, D> {

    D from(@NonNull Buffer buffer);

    Buffer to(@NonNull D data);

    @Override
    default Class<Buffer> fromClass() {
        return Buffer.class;
    }

    @NonNull Class<D> toClass();

    BufferConverter<Buffer> ITSELF = new BufferConverter<Buffer>() {
        @Override
        public @NonNull Class<Buffer> toClass() {
            return Buffer.class;
        }

        @Override
        public Buffer from(@NonNull Buffer buffer) {
            return buffer;
        }

        @Override
        public Buffer to(@NonNull Buffer data) {
            return data;
        }
    };

    BufferConverter<JsonObject> JSON_OBJECT_CONVERTER = new BufferConverter<JsonObject>() {
        @Override
        public @NonNull Class<JsonObject> toClass() {
            return JsonObject.class;
        }

        @Override
        public JsonObject from(@NonNull Buffer buffer) {
            return buffer.length() == 0 ? new JsonObject() : (JsonObject) buffer.toJson();
        }

        @Override
        public Buffer to(@NonNull JsonObject data) {
            return data.toBuffer();
        }
    };

    BufferConverter<JsonArray> JSON_ARRAY_CONVERTER = new BufferConverter<JsonArray>() {
        @Override
        public @NonNull Class<JsonArray> toClass() {
            return JsonArray.class;
        }

        @Override
        public JsonArray from(@NonNull Buffer buffer) {
            return buffer.length() == 0 ? new JsonArray() : (JsonArray) buffer.toJson();
        }

        @Override
        public Buffer to(@NonNull JsonArray data) {
            return data.toBuffer();
        }
    };

}

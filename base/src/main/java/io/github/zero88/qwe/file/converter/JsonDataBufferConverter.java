package io.github.zero88.qwe.file.converter;

import io.github.zero88.qwe.dto.JsonData;
import io.vertx.core.buffer.Buffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonDataBufferConverter<T extends JsonData> implements BufferConverter<T> {

    private final Class<T> clazz;

    @Override
    public @NonNull Class<T> dataClass() {
        return this.clazz;
    }

    @Override
    public T from(@NonNull Buffer buffer) {
        return JsonData.from(buffer.toJson(), dataClass());
    }

    @Override
    public Buffer to(@NonNull T data) {
        return data.toJson().toBuffer();
    }

}

package cloud.playio.qwe.file.converter;

import cloud.playio.qwe.dto.JsonData;
import io.vertx.core.buffer.Buffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonDataBufferConverter<T extends JsonData> implements BufferConverter<T> {

    private final Class<T> clazz;

    @Override
    public @NonNull Class<T> toClass() {
        return this.clazz;
    }

    @Override
    public T from(@NonNull Buffer buffer) {
        return JsonData.from(JSON_OBJECT_CONVERTER.from(buffer), toClass());
    }

    @Override
    public Buffer to(@NonNull T data) {
        return data.toJson().toBuffer();
    }

}

package io.zero88.qwe.dto.jackson;

import io.vertx.core.spi.JsonFactory;
import io.vertx.core.spi.json.JsonCodec;

public final class QWEJsonFactory implements JsonFactory {

    private static final QWEJsonCodec CODEC = new QWEJsonCodec();

    @Override
    public JsonCodec codec() {
        return CODEC;
    }

}

package io.zero88.qwe.event.refl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListenerTest;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.mock.MockEventListener.MockWithVariousParams;

@ExtendWith(VertxExtension.class)
class EventParameterParserTest {

    EventAnnotationProcessor processor;
    EventParameterParser parser;

    @BeforeEach
    void setup(Vertx vertx) {
        processor = EventAnnotationProcessor.create();
        parser = EventParameterParser.create(SharedDataLocalProxy.create(vertx, EventListenerTest.class.getName()),
                                             JsonData.MAPPER);
    }

    @Test
    void test_extract_string() {
        final EventMessage msg = EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", "123"));
        final MethodMeta meta = processor.scan(MockWithVariousParams.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertEquals("123", inputs[0]);
    }

}

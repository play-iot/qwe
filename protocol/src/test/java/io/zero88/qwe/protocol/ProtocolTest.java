package io.zero88.qwe.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProtocolTest {

    @Test
    void test_equals() {
        Assertions.assertEquals(Protocol.AMQP, Protocol.AMQP);
    }

    @Test
    void test_equals_by_factory() {
        Assertions.assertEquals(Protocol.BACnet, Protocol.factory("BACnet"));
    }

    @Test
    void test_not_equals() {
        Assertions.assertNotEquals(Protocol.Kafka, Protocol.AMQP);
    }

    @Test
    void test_not_equals_by_factory() {
        Assertions.assertNotEquals(Protocol.MQTT, Protocol.factory("serial"));
    }

}

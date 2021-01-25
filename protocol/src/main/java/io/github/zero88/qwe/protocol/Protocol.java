package io.github.zero88.qwe.protocol;

import io.github.zero88.qwe.dto.EnumType;
import io.github.zero88.qwe.dto.EnumType.AbstractEnumType;
import io.github.zero88.qwe.dto.PlainType;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a {@code semantic protocol} that express {@code communication protocol}
 */
public final class Protocol extends AbstractEnumType implements PlainType {

    public static final Protocol AMQP = new Protocol("AMQP");
    public static final Protocol BACnet = new Protocol("BACnet");
    public static final Protocol CoAP = new Protocol("CoAP");
    public static final Protocol Modbus = new Protocol("Modbus");
    public static final Protocol Lora = new Protocol("Lora");
    public static final Protocol LwM2M = new Protocol("LwM2M");
    public static final Protocol MQTT = new Protocol("MQTT");
    public static final Protocol Kafka = new Protocol("Kafka");
    public static final Protocol USB = new Protocol("USB");
    public static final Protocol ZigBee = new Protocol("ZigBee");
    public static final Protocol Bluetooth = new Protocol("Bluetooth");
    public static final Protocol Websocket = new Protocol("Websocket");
    public static final Protocol UNKNOWN = new Protocol("UNKNOWN");

    private Protocol(String type) { super(type); }

    public static Protocol def()  { return UNKNOWN; }

    @JsonCreator
    public static Protocol factory(String name) {
        return EnumType.factory(name, Protocol.class, def());
    }

}

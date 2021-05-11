package io.zero88.qwe.protocol;

import java.util.Map;

import io.zero88.qwe.dto.EnumType;
import io.zero88.qwe.dto.JsonData;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.zero88.qwe.protocol.network.IpNetwork;
import io.zero88.qwe.protocol.network.TransportProtocol;
import io.zero88.qwe.protocol.serial.SerialPortProtocol;
import io.zero88.qwe.protocol.usb.UsbProtocol;

import lombok.NonNull;

/**
 * Represents a communication protocol that is a physical layer and could be discovered and realized by
 * computer/machine.
 */
public interface CommunicationProtocol extends EnumType {

    /**
     * Split character is used in separate part in identifier
     */
    String SPLIT_CHAR = "-";

    /**
     * Parse {@code protocol} based on given {@code identifier}
     *
     * @param identifier identifier
     * @return communication protocol
     * @throws IllegalArgumentException if unsupported protocol
     */
    @NonNull
    static CommunicationProtocol parse(@NonNull String identifier) {
        String[] splitter = identifier.split(SPLIT_CHAR, 2);
        if (splitter[0].startsWith("ipv") || splitter.length == 1) {
            return IpNetwork.parse(identifier);
        }
        if (splitter[0].startsWith("udp") || splitter[0].startsWith("tcp")) {
            return TransportProtocol.parse(identifier);
        }
        if (splitter[0].startsWith("serial")) {
            return SerialPortProtocol.parse(identifier);
        }
        if (splitter[0].startsWith("usb")) {
            return UsbProtocol.parse(identifier);
        }
        throw new IllegalArgumentException("Not yet supported protocol " + splitter[0]);
    }

    /**
     * Parse {@code protocol} based on given {@code data map}
     *
     * @param data data map
     * @return communication protocol
     * @throws IllegalArgumentException if unsupported protocol
     */
    @NonNull
    @JsonCreator
    static CommunicationProtocol parse(@NonNull Map<String, Object> data) {
        final String type = Strings.requireNotBlank(data.get("type"), "Missing protocol type");
        if (type.startsWith("ipv")) {
            return JsonData.from(data, IpNetwork.class);
        }
        if (type.startsWith("udp") || type.startsWith("tcp")) {
            return JsonData.from(data, TransportProtocol.class);
        }
        if (type.startsWith("serial")) {
            return JsonData.from(data, SerialPortProtocol.class);
        }
        if (type.startsWith("usb")) {
            return JsonData.from(data, UsbProtocol.class);
        }
        throw new IllegalArgumentException("Not yet supported protocol " + type);
    }

    /**
     * Protocol type
     *
     * @return Protocol type
     */
    @JsonProperty(value = "type")
    @NonNull String type();

    /**
     * Validate current communication protocol is reachable by machine/computer
     * <p>
     * It should be call in {@code runtime} process
     *
     * @return a reference to this, so the API can be used fluently
     * @throws CommunicationProtocolException exception if unreachable
     */
    @NonNull CommunicationProtocol isReachable() throws CommunicationProtocolException;

    /**
     * Unique value to identify protocol, able to use as cache key and able to deserialize from argument.
     * <p>
     * It must be in format {@code type-...}
     *
     * @return communication protocol identifier
     * @see #type()
     * @see #SPLIT_CHAR
     */
    @NonNull String identifier();

}

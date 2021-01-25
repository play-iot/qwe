package io.github.zero88.qwe.protocol.usb;

import io.github.zero88.qwe.exceptions.CommunicationProtocolException;
import io.github.zero88.qwe.protocol.CommunicationProtocol;

import lombok.NonNull;

/**
 * USB protocol
 */
public interface UsbProtocol extends CommunicationProtocol {

    static UsbProtocol parse(@NonNull String key) {
        return null;
    }

    @Override
    UsbProtocol isReachable() throws CommunicationProtocolException;

}

package io.zero88.qwe.protocol;

import lombok.NonNull;

public interface HasProtocol {

    /**
     * Declares IoT protocol
     *
     * @return IoT protocol
     * @see Protocol
     */
    @NonNull Protocol protocol();

}

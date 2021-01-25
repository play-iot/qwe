package io.github.zero88.qwe.protocol.network;

import io.github.zero88.qwe.exceptions.CommunicationProtocolException;
import io.github.zero88.qwe.protocol.CommunicationProtocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

/**
 * Ethernet
 */
public interface Ethernet extends CommunicationProtocol {

    @JsonProperty(value = "type")
    @NonNull String type();

    @Override
    Ethernet isReachable() throws CommunicationProtocolException;

    @Override
    @NonNull
    default String identifier() {
        return String.join(SPLIT_CHAR, type(), getIfName());
    }

    /**
     * Interface index
     *
     * @return interface index of current ethernet
     */
    @JsonProperty(value = "ifIndex")
    Integer getIfIndex();

    /**
     * Interface name
     *
     * @return interface name of current ethernet
     */
    @JsonProperty(value = "ifName")
    String getIfName();

    /**
     * Interface display name
     *
     * @return interface display name of current ethernet
     */
    @JsonProperty(value = "displayName")
    String getDisplayName();

    /**
     * MAC address (a.k.a Physical address) of current interface
     *
     * @return mac address
     */
    @JsonProperty(value = "macAddress")
    String getMacAddress();

    /**
     * CIDR address
     *
     * @return CIDR address
     */
    @JsonProperty(value = "cidrAddress")
    String getCidrAddress();

    /**
     * Host address
     *
     * @return host address
     */
    @JsonProperty(value = "hostAddress")
    String getHostAddress();

}

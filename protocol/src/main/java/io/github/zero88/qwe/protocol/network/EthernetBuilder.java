package io.github.zero88.qwe.protocol.network;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@JsonPOJOBuilder(withPrefix = "")
@SuppressWarnings("unchecked")
abstract class EthernetBuilder<T extends Ethernet, B extends EthernetBuilder> {

    private String type;
    private Integer ifIndex;
    private String ifName;
    private String displayName;
    private String macAddress;
    private String cidrAddress;
    private String hostAddress;

    public abstract T build();

    public B type(String type) {
        this.type = type;
        return (B) this;
    }

    public B ifIndex(Integer index) {
        this.ifIndex = index;
        return (B) this;
    }

    public B ifName(String name) {
        this.ifName = name;
        return (B) this;
    }

    public B displayName(String displayName) {
        this.displayName = displayName;
        return (B) this;
    }

    public B macAddress(String macAddress) {
        this.macAddress = macAddress;
        return (B) this;
    }

    public B cidrAddress(String cidrAddress) {
        this.cidrAddress = cidrAddress;
        return (B) this;
    }

    public B hostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
        return (B) this;
    }

}

package io.github.zero88.msa.bp.cluster;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.dto.msg.RequestData;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class ClusterNode implements JsonData {

    private final String id;
    private final String name;
    private final String address;
    private final String localAddress;

    public RequestData toRequestData() {
        return RequestData.builder().body(toJson()).build();
    }

}

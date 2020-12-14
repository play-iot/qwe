package io.github.zero88.msa.bp.micro;

public enum ServiceKind {

    LOCAL, CLUSTER;

    static ServiceKind parse(String scope) {
        if (CLUSTER.name().equalsIgnoreCase(scope)) {
            return CLUSTER;
        }
        return LOCAL;
    }
}

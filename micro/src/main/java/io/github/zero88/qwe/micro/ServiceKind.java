package io.github.zero88.qwe.micro;

public enum ServiceKind {

    LOCAL, CLUSTER;

    static ServiceKind parse(String scope) {
        if (CLUSTER.name().equalsIgnoreCase(scope)) {
            return CLUSTER;
        }
        return LOCAL;
    }
}

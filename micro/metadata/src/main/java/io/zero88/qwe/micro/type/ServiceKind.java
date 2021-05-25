package io.zero88.qwe.micro.type;

public enum ServiceKind {

    LOCAL, CLUSTER;

    public static ServiceKind parse(String scope) {
        if (CLUSTER.name().equalsIgnoreCase(scope)) {
            return CLUSTER;
        }
        return LOCAL;
    }
}

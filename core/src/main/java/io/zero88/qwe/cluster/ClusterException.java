package io.zero88.qwe.cluster;

import io.zero88.qwe.exceptions.EngineException;
import io.zero88.qwe.exceptions.ErrorCode;

public final class ClusterException extends EngineException {

    public static final ErrorCode CODE = ErrorCode.parse("CLUSTER_ERROR");

    public ClusterException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public ClusterException(String message) { this(message, null); }

    public ClusterException(Throwable e)    { this(null, e); }

}


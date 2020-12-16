package io.github.zero88.msa.bp.cluster;

import io.github.zero88.msa.bp.exceptions.EngineException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;

public final class ClusterException extends EngineException {

    public static final ErrorCode CODE = ErrorCode.parse("CLUSTER_ERROR");

    public ClusterException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public ClusterException(String message) { this(message, null); }

    public ClusterException(Throwable e)    { this(null, e); }

}


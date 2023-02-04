package cloud.playio.qwe.cluster;

import cloud.playio.qwe.exceptions.EngineException;
import cloud.playio.qwe.exceptions.ErrorCode;

public final class ClusterException extends EngineException {

    public static final ErrorCode CODE = ErrorCode.parse("CLUSTER_ERROR");

    public ClusterException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public ClusterException(String message) { this(message, null); }

    public ClusterException(Throwable e)    { this(null, e); }

}


package io.github.zero88.msa.bp.exceptions;

/**
 * Wrap Vertx exceptions
 */
public class EngineException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("ENGINE_ERROR");

    public EngineException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public EngineException(String message) { this(message, null); }

    public EngineException(Throwable e)    { this(null, e); }

    protected EngineException(io.github.zero88.exceptions.ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

}

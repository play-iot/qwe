package cloud.playio.qwe.exceptions;

/**
 * Wrap Vertx exceptions
 */
public class EngineException extends QWEException {

    public EngineException(String message, Throwable e) {
        super(ErrorCode.ENGINE_ERROR, message, e);
    }

    public EngineException(String message) { this(message, null); }

    public EngineException(Throwable e)    { this(null, e); }

    protected EngineException(io.github.zero88.exceptions.ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

}

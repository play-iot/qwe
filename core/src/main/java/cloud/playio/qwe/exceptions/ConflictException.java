package cloud.playio.qwe.exceptions;

public final class ConflictException extends QWEException {

    public ConflictException(String message, Throwable e) {
        super(ErrorCode.CONFLICT_ERROR, message, e);
    }

    public ConflictException(String message) {
        this(message, null);
    }

    public ConflictException(Throwable e) {
        this(null, e);
    }

}

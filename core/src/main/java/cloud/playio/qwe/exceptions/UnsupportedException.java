package cloud.playio.qwe.exceptions;

public final class UnsupportedException extends QWEException {

    public UnsupportedException(String message, Throwable e) {
        super(ErrorCode.UNSUPPORTED, message, e);
    }

    public UnsupportedException(String message) { this(message, null); }

    public UnsupportedException(Throwable e)    { this(null, e); }

}

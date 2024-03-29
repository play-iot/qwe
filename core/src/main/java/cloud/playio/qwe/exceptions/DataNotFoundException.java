package cloud.playio.qwe.exceptions;

public final class DataNotFoundException extends QWEException {

    public DataNotFoundException(String message, Throwable e) {
        super(ErrorCode.DATA_NOT_FOUND, message, e);
    }

    public DataNotFoundException(String message) {
        this(message, null);
    }

    public DataNotFoundException(Throwable e) {
        this("Data not found", e);
    }

}

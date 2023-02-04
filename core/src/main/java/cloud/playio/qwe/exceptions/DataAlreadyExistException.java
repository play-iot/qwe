package cloud.playio.qwe.exceptions;

public final class DataAlreadyExistException extends QWEException {

    public static final ErrorCode CODE = ErrorCode.DATA_ALREADY_EXIST;

    public DataAlreadyExistException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public DataAlreadyExistException(String message) {
        this(message, null);
    }

    public DataAlreadyExistException(Throwable e) {
        this("Data is already existed", e);
    }

}

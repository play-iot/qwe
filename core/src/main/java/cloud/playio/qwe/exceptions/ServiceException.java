package cloud.playio.qwe.exceptions;

public class ServiceException extends QWEException {

    protected ServiceException(ErrorCode code, String message, Throwable e) { super(code, message, e); }

    public ServiceException(String message, Throwable e) {
        this(ErrorCode.SERVICE_ERROR, message, e);
    }

    public ServiceException(String message) { this(message, null); }

    public ServiceException(Throwable e)    { this(null, e); }

}

package cloud.playio.qwe.exceptions;

public class ServiceUnavailable extends ServiceException {

    public ServiceUnavailable(String message, Throwable e) { super(ErrorCode.SERVICE_UNAVAILABLE, message, e); }

    public ServiceUnavailable(String message)              { this(message, null); }

    public ServiceUnavailable(Throwable e)                 { this("Service is unavailable", e); }

}

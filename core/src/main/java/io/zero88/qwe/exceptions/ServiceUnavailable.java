package io.zero88.qwe.exceptions;

public class ServiceUnavailable extends ServiceException {

    public static final ErrorCode CODE = ErrorCode.parse("SERVICE_UNAVAILABLE");

    public ServiceUnavailable(String message, Throwable e) { super(CODE, message, e); }

    public ServiceUnavailable(String message)              { this(message, null); }

    public ServiceUnavailable(Throwable e)                 { this(null, e); }

}

package io.zero88.qwe.exceptions;

public class SecurityException extends QWEException {

    public SecurityException(String message, Throwable e) {
        this(ErrorCode.SECURITY_ERROR, message, e);
    }

    public SecurityException(String message) { this(message, null); }

    public SecurityException(Throwable e)    { this(null, e); }

    public SecurityException(ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

    public static final class AuthenticationException extends SecurityException {

        public AuthenticationException(String message, Throwable e) {
            super(ErrorCode.AUTHENTICATION_ERROR, message, e);
        }

        public AuthenticationException(String message) { this(message, null); }

        public AuthenticationException(Throwable e)    { this(null, e); }

    }


    public static final class InsufficientPermissionError extends SecurityException {

        public InsufficientPermissionError(String message, Throwable e) {
            super(ErrorCode.INSUFFICIENT_PERMISSION_ERROR, message, e);
        }

        public InsufficientPermissionError(String message) { this(message, null); }

        public InsufficientPermissionError(Throwable e)    { this(null, e); }

    }

}

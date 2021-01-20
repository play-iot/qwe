package io.github.zero88.msa.bp.exceptions;

public class SecurityException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("SECURITY_ERROR");

    public SecurityException(String message, Throwable e) {
        this(CODE, message, e);
    }

    public SecurityException(String message) { this(message, null); }

    public SecurityException(Throwable e)    { this(null, e); }

    public SecurityException(ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

    public static final class AuthenticationException extends SecurityException {

        public static final ErrorCode CODE = ErrorCode.parse("AUTHENTICATION_ERROR");

        public AuthenticationException(String message, Throwable e) {
            super(AuthenticationException.CODE, message, e);
        }

        public AuthenticationException(String message) { this(message, null); }

        public AuthenticationException(Throwable e)    { this(null, e); }

    }


    public static final class InsufficientPermissionError extends SecurityException {

        public static final ErrorCode CODE = ErrorCode.parse("INSUFFICIENT_PERMISSION_ERROR");

        public InsufficientPermissionError(String message, Throwable e) {
            super(InsufficientPermissionError.CODE, message, e);
        }

        public InsufficientPermissionError(String message) { this(message, null); }

        public InsufficientPermissionError(Throwable e)    { this(null, e); }

    }

}

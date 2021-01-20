package io.github.zero88.msa.bp.exceptions;

public class InitializerError extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("INITIALIZER_ERROR");

    public InitializerError(String message, Throwable e) {
        super(CODE, message, e);
    }

    public InitializerError(String message) { this(message, null);}

    public InitializerError(Throwable e)    { this(null, e); }

    public static final class MigrationError extends InitializerError {

        public MigrationError(String message, Throwable e) {
            super(message, e);
        }

        public MigrationError(String message) { this(message, null); }

        public MigrationError(Throwable e)    { this(null, e); }

    }

}

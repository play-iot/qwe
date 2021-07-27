package io.zero88.qwe.exceptions;

public class InitializerError extends QWEException {

    protected InitializerError(ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

    public InitializerError(String message, Throwable e) {
        this(ErrorCode.INITIALIZER_ERROR, message, e);
    }

    public InitializerError(String message) { this(message, null);}

    public InitializerError(Throwable e)    { this(null, e); }

    public static class MigrationError extends InitializerError {

        public MigrationError(String message, Throwable e) {
            super(ErrorCode.parse("MIGRATION_ERROR"), message, e);
        }

        public MigrationError(String message) { this(message, null); }

        public MigrationError(Throwable e)    { this(null, e); }

    }

}

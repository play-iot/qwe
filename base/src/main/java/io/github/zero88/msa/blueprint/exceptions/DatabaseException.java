package io.github.zero88.msa.blueprint.exceptions;

public class DatabaseException extends BlueprintException {

    public static ErrorCode DATABASE_ERROR = new ErrorCode("DATABASE_ERROR");

    protected DatabaseException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public DatabaseException(String message, Throwable e) {
        this(DATABASE_ERROR, message, e);
    }

    public DatabaseException(String message) { this(message, null); }

    public DatabaseException(Throwable e)    { this(null, e); }

    public static final class TransactionalException extends DatabaseException {

        public static ErrorCode TRANSACTION_ERROR = new ErrorCode("TRANSACTION_ERROR");

        public TransactionalException(Throwable e) {
            super(TRANSACTION_ERROR, null, e);
        }

    }

}

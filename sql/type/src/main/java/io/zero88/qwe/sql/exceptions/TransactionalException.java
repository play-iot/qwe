package cloud.playio.qwe.sql.exceptions;

import cloud.playio.qwe.exceptions.ErrorCode;

public class TransactionalException extends DatabaseException {

    public static ErrorCode TRANSACTION_ERROR = ErrorCode.parse("TRANSACTION_ERROR");

    protected TransactionalException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public TransactionalException(String message, Throwable e) {
        this(TRANSACTION_ERROR, message, e);
    }

    public TransactionalException(String message) { this(message, null); }

    public TransactionalException(Throwable e)    { this(null, e); }

}

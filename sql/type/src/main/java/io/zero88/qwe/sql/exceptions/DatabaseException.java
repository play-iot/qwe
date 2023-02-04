package cloud.playio.qwe.sql.exceptions;

import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.QWEException;

public class DatabaseException extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("DATABASE_ERROR");

    protected DatabaseException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public DatabaseException(String message, Throwable e) {
        this(CODE, message, e);
    }

    public DatabaseException(String message) { this(message, null); }

    public DatabaseException(Throwable e)    { this(null, e); }

}

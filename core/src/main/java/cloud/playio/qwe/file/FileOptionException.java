package cloud.playio.qwe.file;

import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.exceptions.ErrorCode;

public class FileOptionException extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("FILE_OPTION_ERROR");

    public FileOptionException(String message) {
        super(CODE, message);
    }

    public static FileOptionException disallowCreation() {
        return new FileOptionException("Disallow creating file. Need to enable an auto-create option");
    }

    public static FileOptionException disallowOverwrite() {
        return new FileOptionException("Disallow overwriting file. Need to enable an overwrite option");
    }

}

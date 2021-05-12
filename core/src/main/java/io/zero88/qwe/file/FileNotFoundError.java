package io.zero88.qwe.file;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.ErrorCode;

public final class FileNotFoundError extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("FILE_NOT_FOUND");

    public FileNotFoundError(String message, Throwable e) {
        super(CODE, Strings.fallback(message, "File Not Found"), e);
    }

    public FileNotFoundError(Path path, boolean parentExisted) {
        this("Not found file [" + path + "]" + (parentExisted ? "" : " due to parent is not existed"), null);
    }

    public FileNotFoundError(Path path, Throwable t) {
        this("Not found file [" + path + "]", t);
    }

}

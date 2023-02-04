package cloud.playio.qwe.file;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.exceptions.ErrorCode;

public final class FileAlreadyExistError extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("FILE_ALREADY_EXISTS");

    public FileAlreadyExistError(String message, Throwable e) {
        super(CODE, Strings.fallback(message, "File is already existed"), e);
    }

    public FileAlreadyExistError(Path path, Throwable cause) {
        this("Already existed file [" + path + "]", cause);
    }

}

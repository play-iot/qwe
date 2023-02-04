package cloud.playio.qwe.file;

import io.github.zero88.utils.Strings;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.exceptions.ErrorCode;

public final class FileCorruptedError extends QWEException {

    public static final ErrorCode FILE_CORRUPTED = ErrorCode.parse("FILE_CORRUPTED");

    public FileCorruptedError(String message, Throwable e) {
        super(FILE_CORRUPTED, Strings.fallback(message, "File corrupted"), e);
    }

    public FileCorruptedError(String message) {
        this(message, null);
    }

    public FileCorruptedError(Throwable e) {
        this(null, e);
    }

}

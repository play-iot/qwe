package io.zero88.qwe.file;

import java.nio.file.Path;
import java.util.function.Function;

import io.github.zero88.exceptions.FileException;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.zero88.qwe.file.converter.BufferConverter;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class TextFileOperatorImpl extends AsyncFileOperatorImpl implements TextFileOperator {

    @Override
    public int maxSize() {
        return super.maxSize() == 0 ? DEFAULT_MAX_SIZE : super.maxSize();
    }

    @Override
    public <T> Future<T> read(@NonNull Path path, @NonNull FileOption option, @NonNull BufferConverter<T> converter) {
        final Path p = path.toAbsolutePath();
        return verifyFile(p).flatMap(verified -> {
            if (verified.isExisted()) {
                return fs.readFile(p.toString()).recover(t -> convertException(t, p));
            }
            if (option.isStrict()) {
                return Future.failedFuture(new FileNotFoundError(verified.getPath(), verified.isParentExisted()));
            }
            return touch(p, option).map(f -> Buffer.buffer());
        }).map(buffer -> convert(converter::from, buffer));
    }

    @Override
    public <T> Future<Path> write(@NonNull Path filePath, @NonNull FileOption option, @NonNull T data,
                                  @NonNull BufferConverter<T> converter) {
        final Path path = filePath.toAbsolutePath();
        return verifyFile(path).flatMap(verified -> {
            if (verified.isExisted() && !option.isOverwrite()) {
                return Future.failedFuture(FileOptionException.disallowOverwrite());
            }
            final Future<Path> future = verified.isExisted() ? deleteFile(path, option) : Future.succeededFuture(path);
            return future.flatMap(p -> touch(p, option))
                         .map(p -> convert(converter::to, data))
                         .flatMap(buffer -> fs.writeFile(path.toString(), buffer))
                         .map(unused -> path);
        });
    }

    private <F, T> T convert(@NonNull Function<F, T> func, F from) {
        try {
            return func.apply(from);
        } catch (Exception ex) {
            throw new FileException("Invalid data format", ex);
        }
    }

}

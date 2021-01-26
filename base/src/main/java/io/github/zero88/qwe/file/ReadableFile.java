package io.github.zero88.qwe.file;

import java.nio.file.Path;

import io.github.zero88.qwe.file.converter.BufferConverter;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * File utils for a {@code readable file}, in other words, it is kind of text file
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public class ReadableFile extends AsyncFileHelper {

    @Default
    protected final int maxSize = 10;

    /**
     * Read file to buffer
     *
     * @param filePath File path
     * @param option   File option
     * @return a buffer
     * @apiNote This methods will auto verify a give path must be existed and be a file and a file size doesn't
     *     exceeds max size in configuration
     * @see FileOption
     * @see #verifyFile(Path)
     */
    public Single<Buffer> read(@NonNull Path filePath, @NonNull FileOption option) {
        final Path path = filePath.toAbsolutePath();
        return verifyFile(path).filter(FileVerifiedOutput::isExisted)
                               .flatMap(existed -> fs.rxReadFile(path.toString())
                                                     .onErrorResumeNext(t -> convertException(t, path))
                                                     .map(io.vertx.reactivex.core.buffer.Buffer::getDelegate)
                                                     .toMaybe())
                               .switchIfEmpty(createFile(path, option).map(f -> Buffer.buffer()));
    }

    /**
     * Read file then convert to specific type
     *
     * @param <T>       Type of result
     * @param path      File path
     * @param option    File option
     * @param converter data converter
     * @return a converted object
     * @see #read(Path, FileOption)
     * @see FileOption
     * @see BufferConverter
     */
    public <T> Single<T> readThenConvert(@NonNull Path path, @NonNull FileOption option,
                                         @NonNull BufferConverter<T> converter) {
        return this.read(path, option).flatMap(createDataConverterWrapper(converter::from));
    }

    /**
     * Load file in json array format
     *
     * @param path   File path
     * @param option File option
     * @return json array value
     * @see #readThenConvert(Path, FileOption, BufferConverter)
     * @see FileOption
     */
    public Single<JsonArray> loadArray(@NonNull Path path, @NonNull FileOption option) {
        return readThenConvert(path, option, BufferConverter.JSON_ARRAY_CONVERTER);
    }

    /**
     * Load file in json object format
     *
     * @param path   File path
     * @param option File option
     * @return json object value
     * @see #readThenConvert(Path, FileOption, BufferConverter)
     * @see FileOption
     */
    public Single<JsonObject> loadJson(@NonNull Path path, @NonNull FileOption option) {
        return readThenConvert(path, option, BufferConverter.JSON_OBJECT_CONVERTER);
    }

    /**
     * Write file
     *
     * @param filePath File path
     * @param option   File option
     * @param data     Buffer data
     * @return a reference to path for fluent API
     */
    public Single<Path> write(@NonNull Path filePath, @NonNull FileOption option, @NonNull Buffer data) {
        return writeWithConverter(filePath, option, data, BufferConverter.ITSELF);
    }

    /**
     * Read file then convert to specific type
     *
     * @param filePath  File path
     * @param option    File option
     * @param converter data converter
     * @param <T>       Type of data
     * @return a converted object
     * @see #read(Path, FileOption)
     * @see FileOption
     */
    public <T> Single<Path> writeWithConverter(@NonNull Path filePath, @NonNull FileOption option, @NonNull T data,
                                               @NonNull BufferConverter<T> converter) {
        final Path path = filePath.toAbsolutePath();
        return verifyFile(path).filter(FileVerifiedOutput::isExisted)
                               .flatMap(o -> Single.just(option.isOverwrite())
                                                   .filter(b -> b)
                                                   .flatMapSingleElement(b -> this.deleteFile(path, option)
                                                                                  .flatMap(p -> createFile(p, option)))
                                                   .switchIfEmpty(toFE(FileOptionException.disallowOverwrite()))
                                                   .toMaybe())
                               .switchIfEmpty(createFile(path, option))
                               .flatMap(p -> createDataConverterWrapper(converter::to).apply(data))
                               .map(io.vertx.reactivex.core.buffer.Buffer::newInstance)
                               .flatMapCompletable(d -> fs.rxWriteFile(path.toString(), d))
                               .andThen(Single.just(filePath));
    }

}

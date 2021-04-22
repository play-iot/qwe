package io.zero88.qwe.file;

import java.nio.file.Path;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.file.converter.BufferConverter;

import lombok.NonNull;

@VertxGen
public interface TextFileOperator extends AsyncFileOperator {

    int DEFAULT_MAX_SIZE = 10;

    /**
     * Read file to buffer
     *
     * @param path   File path
     * @param option File option
     * @return a buffer
     * @apiNote This methods will auto verify a give path must be existed and be a file and a file size doesn't
     *     exceeds max size in configuration
     * @see FileOption
     * @see #verifyFile(Path)
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Buffer> read(@NonNull Path path, @NonNull FileOption option) {
        return read(path, option, BufferConverter.ITSELF);
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default TextFileOperator read(@NonNull Path path, @NonNull FileOption option, Handler<AsyncResult<Buffer>> handler) {
        read(path, option).onComplete(handler);
        return this;
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
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T> Future<T> read(@NonNull Path path, @NonNull FileOption option, @NonNull BufferConverter<T> converter);

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T> TextFileOperator read(@NonNull Path path, @NonNull FileOption option, @NonNull BufferConverter<T> converter,
                                      Handler<AsyncResult<T>> handler) {
        read(path, option, converter).onComplete(handler);
        return this;
    }

    /**
     * Load file in json array format
     *
     * @param path   File path
     * @param option File option
     * @return json array value
     * @see #read(Path, FileOption, BufferConverter)
     * @see FileOption
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<JsonArray> loadArray(@NonNull Path path, @NonNull FileOption option) {
        return read(path, option, BufferConverter.JSON_ARRAY_CONVERTER);
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default TextFileOperator loadArray(@NonNull Path path, @NonNull FileOption option,
                                       Handler<AsyncResult<JsonArray>> handler) {
        loadArray(path, option).onComplete(handler);
        return this;
    }

    /**
     * Load file in json object format
     *
     * @param path   File path
     * @param option File option
     * @return json object value
     * @see #read(Path, FileOption, BufferConverter)
     * @see FileOption
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<JsonObject> loadJson(@NonNull Path path, @NonNull FileOption option) {
        return read(path, option, BufferConverter.JSON_OBJECT_CONVERTER);
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default TextFileOperator loadJson(@NonNull Path path, @NonNull FileOption option,
                                      Handler<AsyncResult<JsonObject>> handler) {
        loadJson(path, option).onComplete(handler);
        return this;
    }

    /**
     * Write file
     *
     * @param path   File path
     * @param option File option
     * @param data   Buffer data
     * @return a reference to path for fluent API
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Path> write(@NonNull Path path, @NonNull FileOption option, @NonNull Buffer data) {
        return write(path, option, data, BufferConverter.ITSELF);
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default TextFileOperator write(@NonNull Path path, @NonNull FileOption option, @NonNull Buffer data,
                                   Handler<AsyncResult<Path>> handler) {
        write(path, option, data).onComplete(handler);
        return this;
    }

    /**
     * Write file with converter
     *
     * @param path      File path
     * @param option    File option
     * @param data      Data
     * @param converter Data converter
     * @param <T>       Type of data
     * @return a converted object
     * @see #read(Path, FileOption)
     * @see FileOption
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T> Future<Path> write(@NonNull Path path, @NonNull FileOption option, @NonNull T data,
                           @NonNull BufferConverter<T> converter);

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T> TextFileOperator write(@NonNull Path path, @NonNull FileOption option, @NonNull T data,
                                       @NonNull BufferConverter<T> converter, Handler<AsyncResult<Path>> handler) {
        write(path, option, data, converter).onComplete(handler);
        return this;
    }

}

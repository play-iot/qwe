package io.zero88.qwe.file;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.github.zero88.exceptions.FileException;
import io.github.zero88.utils.FileUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.file.converter.BufferConverter;
import io.zero88.qwe.utils.Configs;

@ExtendWith(VertxExtension.class)
class TextFileOperatorTest {

    TextFileOperator helper;

    @BeforeEach
    void setup(Vertx vertx) {
        helper = TextFileOperatorImpl.builder().vertx(vertx).build();
    }

    @Test
    void disallow_create(VertxTestContext testContext, @TempDir Path tempDir) {
        Path f = tempDir.resolve("1");
        final FileOption option = FileOption.builder().autoCreate(false).build();
        helper.touch(f, option)
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileOptionException);
                  Assertions.assertEquals("Disallow creating file. Need to enable an auto-create option",
                                          err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void make_dir_but_conflict_with_file(VertxTestContext testContext, @TempDir Path tempDir) {
        Path f = tempDir.resolve("1");
        Path d = tempDir.resolve("1").resolve("2");
        final FileOption option = FileOption.create();
        helper.touch(f, option)
              .flatMap(p -> helper.mkdirs(d, option))
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileException);
                  Assertions.assertEquals("One of item in path [" + d + "] is not a directory", err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void read_file_but_give_folder(VertxTestContext testContext, @TempDir Path tempDir) {
        helper.read(tempDir, FileOption.create())
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileException);
                  Assertions.assertEquals("Given path [" + tempDir + "] is not a file", err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void read_not_found_file(VertxTestContext testContext, @TempDir Path tempDir) {
        final Path path = tempDir.resolve("x");
        helper.read(path, FileOption.create())
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileNotFoundError);
                  Assertions.assertEquals("Not found file [" + path + "]", err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void read_file(VertxTestContext testContext, @TempDir Path tempDir) throws IOException {
        final JsonObject object = new JsonObject().put("ab", "cd");
        final Path xy = tempDir.resolve("xy");
        FileUtils.writeToOutputStream(new ByteArrayInputStream(object.toBuffer().getBytes()),
                                      new FileOutputStream(xy.toFile())).close();
        helper.loadJson(xy, FileOption.create()).onSuccess(b -> testContext.verify(() -> {
            System.out.println(b);
            JsonHelper.assertJson(object, b);
            testContext.completeNow();
        })).onFailure(testContext::failNow);
    }

    @Test
    void write_json_array_file(VertxTestContext testContext, @TempDir Path tempDir) {
        final JsonArray object = new JsonArray().add("ab").add("cd");
        final Path axy = tempDir.resolve("axy");
        helper.write(axy, FileOption.create(), object, BufferConverter.JSON_ARRAY_CONVERTER)
              .map(p -> Configs.readAsArray(toStream(p)))
              .onFailure(testContext::failNow)
              .onSuccess(array -> testContext.verify(() -> {
                  System.out.println(array);
                  Assertions.assertEquals(object, array);
                  testContext.completeNow();
              }));
    }

    @Test
    void create_file_is_already_existed(VertxTestContext testContext, @TempDir Path tempDir) {
        final Path a1 = tempDir.resolve("1");
        final FileOption option = FileOption.create();
        helper.touch(a1, option)
              .flatMap(b -> helper.touch(a1, option))
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileAlreadyExistError);
                  Assertions.assertEquals("Already existed file [" + a1 + "]", err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void write_same_file_without_overwrite_option(VertxTestContext testContext, @TempDir Path tempDir) {
        final Path a1 = tempDir.resolve("1");
        final FileOption option = FileOption.builder().overwrite(false).build();
        final BufferConverter<JsonObject> converter = BufferConverter.JSON_OBJECT_CONVERTER;
        helper.write(a1, option, new JsonObject(), converter)
              .flatMap(b -> helper.write(a1, option, new JsonObject().put("1", "2"), converter))
              .onSuccess(b -> testContext.failNow("Expect error in test"))
              .onFailure(err -> testContext.verify(() -> {
                  Assertions.assertTrue(err instanceof FileOptionException);
                  Assertions.assertEquals("Disallow overwriting file. Need to enable an overwrite option",
                                          err.getMessage());
                  testContext.completeNow();
              }));
    }

    @Test
    void write_overwrite_file(VertxTestContext testContext, @TempDir Path tempDir) {
        final Path a1 = tempDir.resolve("1");
        final FileOption option = FileOption.create();
        final BufferConverter<JsonObject> converter = BufferConverter.JSON_OBJECT_CONVERTER;
        helper.write(a1, option, new JsonObject(), converter)
              .flatMap(b -> helper.write(a1, option, new JsonObject().put("1", "2"), converter))
              .map(p -> Configs.readAsJson(toStream(p)))
              .onFailure(testContext::failNow)
              .onSuccess(json -> testContext.verify(() -> {
                  System.out.println(json);
                  JsonHelper.assertJson(new JsonObject().put("1", "2"), json);
                  testContext.completeNow();
              }));
    }

    private FileInputStream toStream(Path p) {
        try {
            return new FileInputStream(p.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

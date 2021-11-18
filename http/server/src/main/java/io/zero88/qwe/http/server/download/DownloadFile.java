package io.zero88.qwe.http.server.download;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;
import io.vertx.core.buffer.Buffer;
import io.zero88.qwe.dto.JsonData;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DownloadFile implements JsonData {

    private String filePath;
    private Buffer content;
    private String fileType;

    public boolean isFile() {
        return Strings.isNotBlank(filePath);
    }

    public static DownloadFile create(Path filePath) {
        return new DownloadFile().setFilePath(filePath.toFile().getAbsolutePath());
    }

    public static DownloadFile create(Buffer content, String fileType) {
        return new DownloadFile().setContent(content).setFileType(fileType);
    }

}

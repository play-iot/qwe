package io.zero88.qwe.http.server.download;

import java.nio.file.Path;

import io.zero88.qwe.eventbus.EBBody;
import io.zero88.qwe.eventbus.EBContract;
import io.zero88.qwe.exceptions.DataNotFoundException;

import lombok.Getter;

public class LocalDownloadFileListener implements DownloadListener {

    @Getter
    private Path downloadDir;

    @EBContract(action = "DOWNLOAD")
    public DownloadFile create(@EBBody("file") String file) {
        final Path filePath = downloadDir.resolve(file);
        if (filePath.toFile().exists()) {
            return DownloadFile.create(filePath);
        }
        throw new DataNotFoundException(decor("Not found file[" + file + "]"));
    }

    @Override
    public DownloadListener setup(Path downloadDir) {
        this.downloadDir = downloadDir;
        return this;
    }

}

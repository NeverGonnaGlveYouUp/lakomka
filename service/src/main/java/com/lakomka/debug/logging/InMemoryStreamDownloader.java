package com.lakomka.debug.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryStreamDownloader {

    private static final int ZIP_COMPRESS_LEVEL = 9;

    private final InMemoryLoggerService loggerService;

    public void createZip(ZipOutputStream zipOutputStream, String fileName) throws IOException {

        List<String> logRecords = loggerService.getLogMessages();

        zipOutputStream.setLevel(ZIP_COMPRESS_LEVEL);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);

        for (String logRecord : logRecords) {
            byte[] logRecordBytes = logRecord.getBytes(StandardCharsets.UTF_8);
            zipOutputStream.write(logRecordBytes, 0, logRecordBytes.length);
        }

        zipOutputStream.closeEntry();

    }

}

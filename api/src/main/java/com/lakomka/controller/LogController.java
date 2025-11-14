package com.lakomka.controller;

import com.lakomka.debug.logging.InMemoryStreamDownloader;
import com.lakomka.util.DateFormatUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;

import static com.lakomka.util.DateFormatUtil.WITH_SECONDS_FORMATTER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LogController {

    private final InMemoryStreamDownloader downloader;

    @GetMapping("/api/system/logs")
    public void downloadLog(final HttpServletResponse response) throws IOException {
        downloadMemLog(response);
    }

    /**
     * Выполняет потоковое скачивание InMemory лога
     */
    private void downloadMemLog(final HttpServletResponse response) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
        final String fileName = "lakomka_" + formattedDateTime + ".log";
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".zip\"");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            downloader.createZip(zipOutputStream, fileName);
            log.info("Download logs as {}", fileName);
            response.flushBuffer();
        }
    }

}

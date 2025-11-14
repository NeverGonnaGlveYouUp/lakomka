package com.lakomka.debug.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.lakomka.services.S3Service;
import com.lakomka.util.DateFormatUtil;
import com.lakomka.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import static com.lakomka.util.DateFormatUtil.WITH_SECONDS_FORMATTER;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryExportIntermitter {

    private final FileUtil fileUtil;
    private final S3Service s3Service;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void store(final List<ILoggingEvent> iLoggingEvents, final Layout<ILoggingEvent> layout) {

        CompletableFuture.runAsync(() -> {
            byte[] byteArray = new byte[0];
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                 GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
                 OutputStreamWriter writer = new OutputStreamWriter(gzipStream)) {

                iLoggingEvents.stream()
                        .map(ev ->
                                Optional.ofNullable(layout)
                                        .map(l -> l.doLayout(ev))
                                        .orElse(ev.getFormattedMessage() + "\n"))
                        .forEach(line -> {
                            try {
                                writer.write(line);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                writer.flush();
                gzipStream.finish();
                byteArray = byteStream.toByteArray();
            } catch (Exception e) {
                log.error("zippingLogs: {}", e.getMessage());
            }

            if (byteArray.length > 0) {
                LocalDateTime now = LocalDateTime.now();
                String formattedDateTime = DateFormatUtil.formatDate(now, WITH_SECONDS_FORMATTER);
                String fileName = "logs/" + formattedDateTime + ".log.gz";
                MultipartFile file = fileUtil.createMultipartFile(fileName, byteArray);
                try {
                    s3Service.uploadFile(file, false);
                } catch (Exception e) {
                    log.error("exportLogs: {}", e.getMessage());
                }
            }

        }, executorService);
    }

}

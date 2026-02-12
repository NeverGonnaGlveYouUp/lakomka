package com.lakomka.services.xml;

import com.lakomka.services.S3Service;
import com.lakomka.services.xml.imports.DiscountXmlParser;
import com.lakomka.services.xml.imports.JPersonXmlParser;
import com.lakomka.services.xml.imports.ProductXmlParser;
import com.lakomka.services.xml.imports.XmlParser;
import com.lakomka.util.DateFormatUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class XmlFileProcessingScheduledService {

    private static final String ERROR_FILE_NAME_INFIX = "_error_";
    private static final String REF_PRODUCT_FILE_NAME_SUFFIX = "_ref_products.xml";
    private static final String REF_JPERSONS_FILE_NAME_SUFFIX = "_ref_jpersons.xml";
    private static final String REF_DISCOUNTS_FILE_NAME_SUFFIX = "_ref_discounts.xml";
    private final Map<String, XmlParser> xmlParserMap = new HashMap<>();
    private final S3Service s3Service;

    @Value("${app.scheduling.enabled:true}")
    private boolean schedulingEnabled;

    @PostConstruct
    public void init() {
        checkAndProcessFile();
    }

    @Autowired
    private XmlFileProcessingScheduledService(
            S3Service s3Service,
            ProductXmlParser productXmlParser,
            JPersonXmlParser jPersonXmlParser,
            DiscountXmlParser discountXmlParser
    ) {
        this.s3Service = s3Service;
        s3Service.listFiles().stream()
                .filter(s ->
                        s.endsWith(REF_PRODUCT_FILE_NAME_SUFFIX) ||
                        s.endsWith(REF_JPERSONS_FILE_NAME_SUFFIX) ||
                        s.endsWith(REF_DISCOUNTS_FILE_NAME_SUFFIX))
                .forEach(s -> {
                    if (s.endsWith(REF_PRODUCT_FILE_NAME_SUFFIX)) {
                        xmlParserMap.put(s, productXmlParser);
                    } else if (s.endsWith(REF_JPERSONS_FILE_NAME_SUFFIX)) {
                        xmlParserMap.put(s, jPersonXmlParser);
                    } else if (s.endsWith(REF_DISCOUNTS_FILE_NAME_SUFFIX)) {
                        xmlParserMap.put(s, discountXmlParser);
                    }
                });
    }

    /**
     * Cron expression based on active profile
     * - Dev: every 1 minute
     * - Prod: every 5 minutes
     */
    @Scheduled(cron = "#{@schedulingProperties.getCronExpression()}")
    public void checkAndProcessFile() {

        if (!schedulingEnabled) {
            log.debug("Scheduling is disabled, skipping file check");
            return;
        }

        for (String fileName : xmlParserMap.keySet()) {

            log.debug("Checking for file: {}", fileName);

            try {

                // Check if file exists in S3
                if (!s3Service.fileExists(fileName)) {
                    continue;
                }

                // Download the file
                log.info("Downloading file: {}", fileName);
                byte[] fileContent = s3Service.downloadFile(fileName);

                if (fileContent == null || fileContent.length == 0) {
                    log.error("Downloaded file is empty or null: {}", fileName);
                    handleProcessingError(fileName);
                    continue;
                }

                XmlParser xmlParser = xmlParserMap.get(fileName);

                // Process the file
                log.info("Sending file to {} for processing", xmlParser.getClass().getSimpleName());

                if (xmlParser.parse(fileContent)) {
//                    log.info("File processed successfully. Deleting file: {}", fileName);
//                    s3Service.deleteFile(fileName);
                } else {
                    log.error("File processing failed for: {}", fileName);
                    handleProcessingError(fileName);
                }

            } catch (Exception e) {
                log.error("Error during file processing for {}: {}", xmlParserMap, e.getMessage());
                handleProcessingError(fileName);
            }

        }
    }

    private void handleProcessingError(String fileName) {
        String errFileName = dateTimedFileName(fileName);
        log.info("Attempting to rename file to: {}", errFileName);
        s3Service.renameFile(fileName, errFileName);
    }

    private String dateTimedFileName(@NonNull String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        String baseName = fileName.substring(0, lastDotIndex);
        String fileExtension = fileName.substring(lastDotIndex);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = DateFormatUtil.formatDate(now, DateFormatUtil.WITH_SECONDS_FORMATTER);
        return baseName + ERROR_FILE_NAME_INFIX + formattedDateTime + fileExtension;
    }

}
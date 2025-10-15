package com.lakomka.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileProcessingScheduledService {

    private static final String FILE_NAME = "ref_products.xml";
    private static final String ERROR_FILE_NAME = "ref_products_error.xml";

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProductXmlParser productXmlParser;

    @Value("${app.scheduling.enabled:true}")
    private boolean schedulingEnabled;

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

        log.debug("Checking for file: {}", FILE_NAME);

        try {

            // Check if file exists in S3
            if (!s3Service.fileExists(FILE_NAME)) {
                return;
            }

            // Download the file
            log.info("Downloading file: {}", FILE_NAME);
            byte[] fileContent = s3Service.downloadFile(FILE_NAME);

            if (fileContent == null || fileContent.length == 0) {
                log.error("Downloaded file is empty or null: {}", FILE_NAME);
                handleProcessingError();
                return;
            }

            // Process the file
            log.info("Sending file to {} for processing", productXmlParser.getClass().getSimpleName());

            if (productXmlParser.parse(fileContent)) {
                log.info("File processed successfully. Deleting file: {}", FILE_NAME);
                if (s3Service.deleteFile(FILE_NAME)) {
                    log.info("File {} successfully deleted", FILE_NAME);
                } else {
                    log.error("Failed to delete file: {}", FILE_NAME);
                }
            } else {
                log.error("File processing failed for: {}", FILE_NAME);
                handleProcessingError();
            }

        } catch (Exception e) {
            log.error("Error during file processing for {}: {}", FILE_NAME, e.getMessage(), e);
            handleProcessingError();
        }
    }

    private void handleProcessingError() {
        log.info("Attempting to rename file to: {}", ERROR_FILE_NAME);
        if (s3Service.renameFile(FILE_NAME, ERROR_FILE_NAME)) {
            log.info("File successfully renamed to: {}", ERROR_FILE_NAME);
        } else {
            log.error("Failed to rename file to: {}", ERROR_FILE_NAME);
        }
    }

}
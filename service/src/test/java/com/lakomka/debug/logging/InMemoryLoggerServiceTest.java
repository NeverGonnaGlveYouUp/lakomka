package com.lakomka.debug.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class InMemoryLoggerServiceTest {

    private InMemoryLoggerService loggerService;

    @BeforeEach
    void setUp() {
        InMemoryExportIntermitter mock = Mockito.mock(InMemoryExportIntermitter.class);
        loggerService = new InMemoryLoggerService(mock);
        loggerService.clearLogs();
    }

    @Test
    void testEnableInMemoryLogging() {
        // When
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.debug("Test debug message");
        logger.info("Test info message");

        // Then
        List<ILoggingEvent> logs = loggerService.getLogs();
        assertEquals(2, logs.size());

        List<String> messages = loggerService.getLogMessages();
        assertTrue(messages.stream().anyMatch(line -> line.contains("Test debug message")));
        assertTrue(messages.stream().anyMatch(line -> line.contains("Test info message")));
    }

    @Test
    void testClearLogs() {
        // Given
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        logger.info("Test message");

        // When
        loggerService.clearLogs();

        // Then
        assertTrue(loggerService.getLogs().isEmpty());
    }

    @Test
    void testGetLogMessages() {
        // Given
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        logger.setLevel(Level.WARN);
        logger.warn("Warning message");
        logger.error("Error message");

        // When
        List<String> messages = loggerService.getLogMessages();

        // Then
        assertEquals(2, messages.size());
        assertTrue(messages.stream().anyMatch(line -> line.contains("Warning message")));
        assertTrue(messages.stream().anyMatch(line -> line.contains("Error message")));
    }
}
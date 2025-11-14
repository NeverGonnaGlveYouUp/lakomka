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
        // Given
        loggerService.enableInMemoryLogging("com.example.test", Level.DEBUG);

        // When
        Logger logger = (Logger) LoggerFactory.getLogger("com.example.test");
        logger.debug("Test debug message");
        logger.info("Test info message");

        // Then
        List<ILoggingEvent> logs = loggerService.getLogs();
        assertEquals(2, logs.size());

        List<String> messages = loggerService.getLogMessages();
        assertTrue(messages.contains("Test debug message"));
        assertTrue(messages.contains("Test info message"));
    }

    @Test
    void testClearLogs() {
        // Given
        loggerService.enableInMemoryLogging("com.example.test", Level.INFO);
        Logger logger = (Logger) LoggerFactory.getLogger("com.example.test");
        logger.info("Test message");

        // When
        loggerService.clearLogs();

        // Then
        assertTrue(loggerService.getLogs().isEmpty());
    }

    @Test
    void testGetLogMessages() {
        // Given
        loggerService.enableInMemoryLogging("com.example.test", Level.WARN);
        Logger logger = (Logger) LoggerFactory.getLogger("com.example.test");
        logger.warn("Warning message");
        logger.error("Error message");

        // When
        List<String> messages = loggerService.getLogMessages();

        // Then
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Warning message"));
        assertTrue(messages.contains("Error message"));
    }
}
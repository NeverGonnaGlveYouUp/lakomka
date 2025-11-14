package com.lakomka.debug.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.qos.logback.classic.Level.*;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

@Service
@Slf4j
public class InMemoryLoggerService {

    @Value("${logging.level.root:info}")
    private String level;

    private final LoggerContext loggerContext;
    private final InMemoryAppender inMemoryAppender;

    public InMemoryLoggerService(InMemoryExportIntermitter intermitter) {
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.inMemoryAppender = new InMemoryAppender(intermitter, this.loggerContext);
        this.inMemoryAppender.setName("IN_MEMORY_APPENDER");
        this.inMemoryAppender.start();
        enableInMemoryLogging(ROOT_LOGGER_NAME, parseLevel());
        log.info("Initialized IN_MEMORY_APPENDER");
    }

    public void enableInMemoryLogging(String loggerName, Level level) {
        Logger logger = loggerContext.getLogger(loggerName);
        logger.addAppender(inMemoryAppender);
        logger.setLevel(level);
        logger.setAdditive(true);
    }

    public List<ILoggingEvent> getLogs() {
        return inMemoryAppender.getEvents();
    }

    public List<String> getLogMessages() {
        return inMemoryAppender.getEvents().stream()
                .map(ev ->
                        Optional.ofNullable(inMemoryAppender.getLayout())
                                .map(l -> l.doLayout(ev))
                                .orElse(ev.getFormattedMessage() + "\n"))
                .collect(Collectors.toList());
    }

    public void clearLogs() {
        inMemoryAppender.clear();
    }

    public void disableInMemoryLogging(String loggerName) {
        Logger logger = loggerContext.getLogger(loggerName);
        logger.detachAppender(inMemoryAppender);
    }

    private Level parseLevel() {
        List<Level> levels = List.of(OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL);
        return levels.stream()
                .filter(l -> l.levelStr.equalsIgnoreCase(level))
                .findFirst()
                .orElse(INFO);
    }

}

package com.lakomka.debug.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {

    public static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{32} - %msg%n";
    private static final int MAX_EVENTS = 10000;
    private final List<ILoggingEvent> events = new ArrayList<>();
    private final AppenderAttachableImpl<ILoggingEvent> appenderAttachable = new AppenderAttachableImpl<>();
    private final InMemoryExportIntermitter intermitter;
    private final LoggerContext loggerContext;
    private int currentEventCount = 0;
    private boolean isFull = false;
    @Setter
    @Getter
    private Layout<ILoggingEvent> layout;

    public InMemoryAppender(InMemoryExportIntermitter intermitter, LoggerContext loggerContext) {
        this.intermitter = intermitter;
        this.loggerContext = loggerContext;
    }

    @Override
    public void start() {
        if (layout == null) {
            // Default pattern layout init
            PatternLayout patternLayout = new PatternLayout();
            patternLayout.setPattern(DEFAULT_PATTERN);
            patternLayout.setContext(loggerContext);
            patternLayout.start();
            layout = patternLayout;
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        synchronized (events) {
            events.add(event);
            currentEventCount++;
            if (!isFull && currentEventCount >= MAX_EVENTS) {
                isFull = true;
                // offload to s3 and partially clear
                intermitter.store(new ArrayList<>(events.subList(0, MAX_EVENTS)), layout);
                clearToMax();
            }
        }
    }

    @Override
    public void addAppender(Appender<ILoggingEvent> appender) {

    }

    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return null;
    }

    @Override
    public Appender<ILoggingEvent> getAppender(String name) {
        return appenderAttachable.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return false;
    }

    @Override
    public void detachAndStopAllAppenders() {

    }

    @Override
    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return false;
    }

    @Override
    public boolean detachAppender(String name) {
        return appenderAttachable.detachAppender(name);
    }

    public List<ILoggingEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void clear() {
        synchronized (events) {
            events.clear();
            currentEventCount = 0;
            isFull = false;
        }
    }

    private void clearToMax() {
        // Create a sublist from index MAX_EVENTS to end
        List<ILoggingEvent> remainingElements = new ArrayList<>(events.subList(MAX_EVENTS, events.size()));
        // Clear original list and add remaining elements
        events.clear();
        events.addAll(remainingElements);
        currentEventCount = 0;
        isFull = false;
    }

}
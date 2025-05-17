package ru.tsavaph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnabledTimeTrackerServiceTest {


    EnabledTimeTrackerService service;

    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        service = new EnabledTimeTrackerService();
        var logger = (Logger) LoggerFactory.getLogger(TimeTrackerThreadContext.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void trackTime_thresholdPassed_loggedMethodExecutionInfo() {
        var info = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                1,
                ChronoUnit.NANOS
        );
        var result = service.trackTime(info, () -> {
            try {
                service.trackTime(info, () -> {
                });
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "result";
        });

        assertEquals("result", result);
        assertEquals(1, listAppender.list.size());
        var loggedInfo = listAppender.list.get(0);
        var lines = loggedInfo.getFormattedMessage().split("\n");
        assertEquals(3, lines.length);
        assertEquals("", lines[0]);
        assertThat(lines[1]).matches(" private void testMethod\\(String a, int b\\) - \\d+ ns\\. Arguments: a=test, b=1");
        assertThat(lines[2]).matches(" {2}\\|-- private void testMethod\\(String a, int b\\) - \\d+ ns\\. Arguments: a=test, b=1");
        assertEquals(Level.INFO, loggedInfo.getLevel());
    }

    @Test
    void trackTime_thresholdNotPassed_notLoggedMethodExecutionInfo() {
        var info = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                15,
                ChronoUnit.MILLIS
        );
        var result = service.trackTime(info, () -> {
            try {
                service.trackTime(info, () -> {
                });
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "result";
        });

        assertEquals("result", result);
        assertEquals(1, listAppender.list.size());
        var loggedInfo = listAppender.list.get(0);
        assertEquals("", loggedInfo.getFormattedMessage());
    }

}

package ru.tsavaph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link EnabledTimeTrackerService}
 */
class EnabledTimeTrackerServiceTest {


    TimeTrackerService service;

    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        service = new EnabledTimeTrackerService(org.slf4j.event.Level.ERROR);
        Logger logger = (Logger) LoggerFactory.getLogger(TimeTrackerThreadContext.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void trackTime_thresholdPassed_loggedMethodExecutionInfo() {
        TimeTrackerInfo info = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                1,
                ChronoUnit.NANOS
        );
        String result = service.trackTime(info, () -> {
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
        ILoggingEvent loggedInfo = listAppender.list.get(0);
        String[] lines = loggedInfo.getFormattedMessage().split("\n");
        assertEquals(3, lines.length);
        assertEquals("", lines[0]);
        assertThat(lines[1]).matches(" private void testMethod\\(String a, int b\\) - \\d+ ns\\. Arguments: a=test, b=1");
        assertThat(lines[2]).matches(" {2}\\|-- private void testMethod\\(String a, int b\\) - \\d+ ns\\. Arguments: a=test, b=1");
        assertEquals(Level.ERROR, loggedInfo.getLevel());
    }

    @Test
    void trackTime_thresholdNotPassed_notLoggedMethodExecutionInfo() {
        TimeTrackerInfo info = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                15,
                MILLIS
        );
        String result = service.trackTime(info, () -> {
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
        assertTrue(listAppender.list.isEmpty());
    }

}

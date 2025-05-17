package ru.tsavaph;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DisabledTimeTrackerServiceTest {

    DisabledTimeTrackerService service = new DisabledTimeTrackerService();
    TimeTrackerInfo info = mock(TimeTrackerInfo.class);

    @Test
    void trackTime_shouldReturnValue() {
        var result = service.trackTime(info, () -> "result");

        assertEquals("result", result);
    }

    @Test
    void trackTime_shouldPassThroughException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.trackTime(info, () -> {
                throw new RuntimeException("fail");
            });
        });

        assertEquals("fail", exception.getMessage());
    }

    @Test
    void trackTimeRunnable_shouldRunSuccessfully() {
        var wasRun = new AtomicBoolean(false);

        service.trackTime(info, () -> wasRun.set(true));

        assertTrue(wasRun.get());
    }

    @Test
    void trackTimeRunnable_shouldPropagateException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.trackTime(info, () -> {
                throw new RuntimeException("fail");
            });
        });

        assertEquals("fail", exception.getMessage());
    }
}

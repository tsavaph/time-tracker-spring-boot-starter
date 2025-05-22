package ru.tsavaph.configuration;

import org.junit.jupiter.api.Test;
import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit test for {@link DisabledTimeTrackerService}
 */
class DisabledTimeTrackerServiceTest {

    TimeTrackerService service = new DisabledTimeTrackerService();
    TimeTrackerInfo info = mock(TimeTrackerInfo.class);

    @Test
    void trackTime_shouldReturnValue() {
        String result = service.trackTime(info, () -> "result");

        assertEquals("result", result);
    }

    @Test
    void trackTime_shouldPassThroughException() {
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> service.trackTime(info, () -> {
                    throw new RuntimeException("fail");
                })
        );

        assertEquals("fail", exception.getMessage());
    }

    @Test
    void trackTimeRunnable_shouldRunSuccessfully() {
        AtomicBoolean wasRun = new AtomicBoolean(false);

        service.trackTime(info, () -> wasRun.set(true));

        assertTrue(wasRun.get());
    }

    @Test
    void trackTimeRunnable_shouldPropagateException() {
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> service.trackTime(info, () -> {
                    throw new RuntimeException("fail");
                })
        );
        assertEquals("fail", exception.getMessage());
    }
}

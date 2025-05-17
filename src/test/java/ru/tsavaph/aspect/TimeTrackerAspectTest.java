package ru.tsavaph.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link TimeTrackerAspect}
 */
@ExtendWith(MockitoExtension.class)
class TimeTrackerAspectTest {

    @Mock
    TimeTrackerService timeTrackerService;

    @InjectMocks
    TimeTrackerAspect timeTrackerAspect;

    @Test
    void trackTime_serviceCalled() throws NoSuchMethodException {

        var method = this.getClass().getDeclaredMethod("testMethod", String.class, int.class);
        var methodSignature = mock(MethodSignature.class);
        when(methodSignature.getMethod()).thenReturn(method);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"a", "b"});
        var joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test", 1});
        var expectedResult = "Expected";
        when(timeTrackerService.trackTime(any(), Mockito.<Supplier<String>>any())).thenReturn(expectedResult);

        var result = timeTrackerAspect.trackTime(joinPoint);

        var expectedTimeTrackerInfo = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                100,
                ChronoUnit.NANOS
        );
        verify(timeTrackerService).trackTime(eq(expectedTimeTrackerInfo), Mockito.<Supplier<String>>any());
        assertEquals(expectedResult, result);
    }

    @TimeTracker(timeUnit = ChronoUnit.NANOS, argumentsIncluded = true, timeThreshold = 100)
    private void testMethod(String a, int b) {

    }

}
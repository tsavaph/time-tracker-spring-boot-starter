package ru.tsavaph.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.lang.reflect.Method;
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

    @Mock
    Environment environment;

    @InjectMocks
    TimeTrackerAspect timeTrackerAspect;

    @Test
    void trackTime_noPropertyTimeThreshold_serviceCalled() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("testMethod", String.class, int.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(methodSignature.getMethod()).thenReturn(method);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"a", "b"});
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test", 1});
        String expectedResult = "Expected";
        when(timeTrackerService.trackTime(any(), Mockito.<Supplier<String>>any())).thenReturn(expectedResult);

        Object result = timeTrackerAspect.trackTime(joinPoint);

        TimeTrackerInfo expectedTimeTrackerInfo = new TimeTrackerInfo(
                "private void testMethod(String a, int b)",
                "a=test, b=1",
                true,
                100,
                ChronoUnit.NANOS
        );
        verify(timeTrackerService).trackTime(eq(expectedTimeTrackerInfo), Mockito.<Supplier<String>>any());
        assertEquals(expectedResult, result);
    }

    @Test
    void trackTime_propertyTimeThreshold_serviceCalled() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("propertyTimeThresholdTestMethod", String.class, long.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(methodSignature.getMethod()).thenReturn(method);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"c", "d"});
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test", 1});
        String expectedResult = "Expected";
        when(timeTrackerService.trackTime(any(), Mockito.<Supplier<String>>any())).thenReturn(expectedResult);
        int timeThreshold = 5;
        when(environment.resolvePlaceholders("${test.prop}")).thenReturn(String.valueOf(timeThreshold));

        Object result = timeTrackerAspect.trackTime(joinPoint);

        TimeTrackerInfo expectedTimeTrackerInfo = new TimeTrackerInfo(
                "private void propertyTimeThresholdTestMethod(String c, long d)",
                "c=test, d=1",
                true,
                timeThreshold,
                ChronoUnit.MICROS
        );
        verify(timeTrackerService).trackTime(eq(expectedTimeTrackerInfo), Mockito.<Supplier<String>>any());
        assertEquals(expectedResult, result);
    }

    @TimeTracker(timeUnit = ChronoUnit.NANOS, argumentsIncluded = true, timeThreshold = 100)
    private void testMethod(String a, int b) {

    }

    @TimeTracker(timeUnit = ChronoUnit.MICROS, argumentsIncluded = true, timeThreshold = -5, propertyTimeThreshold = "${test.prop}")
    private void propertyTimeThresholdTestMethod(String c, long d) {

    }

}
package ru.tsavaph;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.*;
import static ru.tsavaph.TimeTrackerConstant.EMPTY_STRING;
import static ru.tsavaph.TimeTrackerConstant.NO_TIME_THRESHOLD;

/**
 * Unit test for {@link TimeTrackerInfo}
 */
class TimeTrackerInfoTest {
    
    String methodName = "methodName";
    String methodArguments = "methodName";
    int timeThreshold = 500;
    ChronoUnit timeUnit = ChronoUnit.MICROS;
    
    @Test
    void constructor_withNameThresholdAndUnit_shouldDefaultArgumentsIncludedToFalse() {
        TimeTrackerInfo info = new TimeTrackerInfo(methodName, timeThreshold, timeUnit);

        assertEquals(methodName, info.methodName());
        assertEquals(EMPTY_STRING, info.methodArguments());
        assertFalse(info.argumentsIncluded());
        assertEquals(timeThreshold, info.timeThreshold());
        assertEquals(timeUnit, info.timeUnit());
    }

    @Test
    void constructor_withNameAndThreshold_shouldDefaultToMillisAndNoArguments() {
        TimeTrackerInfo info = new TimeTrackerInfo(methodName, timeThreshold);

        assertEquals(methodName, info.methodName());
        assertEquals(EMPTY_STRING, info.methodArguments());
        assertFalse(info.argumentsIncluded());
        assertEquals(timeThreshold, info.timeThreshold());
        assertEquals(MILLIS, info.timeUnit());
    }

    @Test
    void constructor_withOnlyName_shouldSetDefaultsCorrectly() {
        TimeTrackerInfo info = new TimeTrackerInfo(methodName);

        assertEquals(methodName, info.methodName());
        assertEquals(EMPTY_STRING, info.methodArguments());
        assertFalse(info.argumentsIncluded());
        assertEquals(NO_TIME_THRESHOLD, info.timeThreshold());
        assertEquals(MILLIS, info.timeUnit());
    }

    @Test
    void constructor_withNameArgumentsAndThreshold_shouldIncludeArgumentsAndSetMillis() {
        TimeTrackerInfo info = new TimeTrackerInfo(methodName, methodArguments, timeThreshold);

        assertEquals(methodName, info.methodName());
        assertEquals(methodArguments, info.methodArguments());
        assertTrue(info.argumentsIncluded());
        assertEquals(timeThreshold, info.timeThreshold());
        assertEquals(MILLIS, info.timeUnit());
    }

    @Test
    void constructor_withNameArgumentsAndUnit_shouldIncludeArgumentsAndUseUnit() {
        TimeTrackerInfo info = new TimeTrackerInfo(methodName, methodArguments, timeUnit);

        assertEquals(methodName, info.methodName());
        assertEquals(methodArguments, info.methodArguments());
        assertTrue(info.argumentsIncluded());
        assertEquals(NO_TIME_THRESHOLD, info.timeThreshold());
        assertEquals(timeUnit, info.timeUnit());
    }
}

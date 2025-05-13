package ru.tsavaph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.temporal.ChronoUnit;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TimeTrackerMethodContext {

    private final int pointerDepth;
    private final String methodName;
    private final String arguments;
    private final boolean argumentsIncluded;
    private final int timeThreshold;
    private final ChronoUnit timeUnit;

    private long executionTimeNanos;


    public boolean isTimeThresholdExceeded() {
        return timeThreshold == TimeTrackerConstant.NO_TIME_THRESHOLD || getExecutionTimeInTimeUnit() > timeThreshold;
    }

    public long getExecutionTimeInTimeUnit() {
        if (ChronoUnit.NANOS.equals(timeUnit)) {
            return executionTimeNanos;
        }
        if (ChronoUnit.MICROS.equals(timeUnit)) {
            return executionTimeNanos / 1000;
        }
        if (ChronoUnit.MILLIS.equals(timeUnit)) {
            return executionTimeNanos / 1_000_000;
        }
        if (ChronoUnit.SECONDS.equals(timeUnit)) {
            return executionTimeNanos / 1_000_000_000;
        }
        throw new IllegalStateException("Time unit not supported");
    }

    public String getTimeUnitString() {
        if (ChronoUnit.NANOS.equals(timeUnit)) {
            return "ns";
        }
        if (ChronoUnit.MICROS.equals(timeUnit)) {
            return "µs";
        }
        if (ChronoUnit.MILLIS.equals(timeUnit)) {
            return "ms";
        }
        if (ChronoUnit.SECONDS.equals(timeUnit)) {
            return "s";
        }
        throw new IllegalStateException("Time unit not supported");
    }

}

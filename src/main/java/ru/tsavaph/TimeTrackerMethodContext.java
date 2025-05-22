package ru.tsavaph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.temporal.ChronoUnit;

/**
 * Context for tracking method or block of code execution.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TimeTrackerMethodContext {

    private final int pointerDepth;
    private final TimeTrackerInfo timeTrackerInfo;
    private long executionTimeNanos;

    /**
     * Shows if execution time greater than set threshold.
     *
     * @return {code true} if threshold disabled or execution time more than threshold.
     */
    public boolean isTimeThresholdExceeded() {
        int timeThreshold = timeTrackerInfo.timeThreshold();
        return timeThreshold == TimeTrackerConstant.NO_TIME_THRESHOLD || getExecutionTimeInTimeUnit() > timeThreshold;
    }

    /**
     * Execution time.
     *
     * @return execution time in set time units.
     */
    public long getExecutionTimeInTimeUnit() {
        ChronoUnit timeUnit = timeTrackerInfo.timeUnit();
        return switch (timeUnit) {
            case NANOS -> executionTimeNanos;
            case MICROS -> executionTimeNanos / 1_000;
            case MILLIS -> executionTimeNanos / 1_000_000;
            case SECONDS -> executionTimeNanos / 1_000_000_000;
            default -> throw new IllegalStateException("Time unit not supported: " + timeUnit);
        };
    }

    /**
     * Time units symbol for logs.
     *
     * @return beautiful representation of set time units. For example 'ns' for {@link ChronoUnit#NANOS}.
     */
    public String getTimeUnitString() {
        ChronoUnit timeUnit = timeTrackerInfo.timeUnit();
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

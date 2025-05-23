package ru.tsavaph;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.temporal.ChronoUnit;
import java.util.Set;


/**
 * Constats for TimeTracker.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class TimeTrackerConstant {

    /**
     * Empty string.
     */
    public static final String EMPTY_STRING = "";

    /**
     * Disabled method execution time threshold.
     */
    public static final int NO_TIME_THRESHOLD = -1;

    /**
     * Disabled method execution time threshold.
     */
    public static final String NO_PROPERTY_THRESHOLD = "-1";

    /**
     * Space string.
     */
    public static final String SPACE = " ";

    /**
     * Symbol used to offset child execution time in logs.
     */
    public static final String OFFSET_SYMBOL = "|--";

    /**
     * Allowed time units.
     */
    public static final Set<ChronoUnit> ALLOWED_TIME_UNITS =
            Set.of(ChronoUnit.NANOS, ChronoUnit.MICROS, ChronoUnit.MILLIS, ChronoUnit.SECONDS);

}

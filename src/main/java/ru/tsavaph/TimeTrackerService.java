package ru.tsavaph;

import ru.tsavaph.aspect.TimeTracker;

import java.util.function.Supplier;

/**
 * Used for measurement and logging execution time. See {@link TimeTracker} for example.
 */
public interface TimeTrackerService {

    /**
     * Measure and log provided supplier's execution time with provided info.
     *
     * @param timeTrackerInfo provided additional info for logging.
     * @param supplier        provided supplier for measurement.
     * @param <T>             the type of results supplied by this supplier
     * @return supplier's result.
     */
    <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier);

    /**
     * Measure and log provided runnable's execution time with provided info.
     *
     * @param timeTrackerInfo provided additional info for logging.
     * @param runnable        provided runnable for measurement.
     */
    void trackTime(TimeTrackerInfo timeTrackerInfo, Runnable runnable);

}

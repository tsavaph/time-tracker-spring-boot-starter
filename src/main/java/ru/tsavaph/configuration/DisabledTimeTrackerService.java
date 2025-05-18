package ru.tsavaph.configuration;

import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.util.function.Supplier;

/**
 * Dummy bean when other {@link TimeTrackerService} bean is missing.
 */
class DisabledTimeTrackerService implements TimeTrackerService {

    public <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier) {
        return supplier.get();
    }

    public void trackTime(TimeTrackerInfo timeTrackerInfo, Runnable runnable) {
        runnable.run();
    }

}

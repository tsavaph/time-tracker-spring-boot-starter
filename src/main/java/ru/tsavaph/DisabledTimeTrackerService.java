package ru.tsavaph;

import java.util.function.Supplier;

public class DisabledTimeTrackerService implements TimeTrackerService {

    public <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier) {
        return supplier.get();
    }

    public void trackTime(TimeTrackerInfo timeTrackerInfo, Runnable runnable) {
        runnable.run();
    }

}

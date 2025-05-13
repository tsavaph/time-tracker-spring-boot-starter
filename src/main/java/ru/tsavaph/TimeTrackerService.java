package ru.tsavaph;

import java.util.function.Supplier;

public interface TimeTrackerService {

    <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier);

    void trackTime(TimeTrackerInfo timeTrackerInfo, Runnable runnable);

}

package ru.tsavaph;

import java.time.temporal.ChronoUnit;

public record TimeTrackerInfo(
        String methodName,
        String methodArguments,
        boolean argumentsIncluded,
        int timeThreshold,
        ChronoUnit timeUnit) {

    public TimeTrackerInfo(String methodName, int timeThreshold, ChronoUnit timeUnit) {
        this(methodName, TimeTrackerConstant.EMPTY_STRING, false, timeThreshold, timeUnit);
    }

}

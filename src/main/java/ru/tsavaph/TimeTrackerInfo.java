package ru.tsavaph;

import java.time.temporal.ChronoUnit;

/**
 * Method info and options for logging its time execution.
 * @param methodName name of the method or a block of code. Example: private void parent(int a, int b).
 * @param methodArguments arguments of the method or a block code. Example: a=1, b=2.
 * @param argumentsIncluded include arguments into log or not.
 * @param timeThreshold threshold for logging. If method execution time is below the threshold, it won't be logged.
 * @param timeUnit time unit for logging execution time.
 */
public record TimeTrackerInfo(
        String methodName,
        String methodArguments,
        boolean argumentsIncluded,
        int timeThreshold,
        ChronoUnit timeUnit) {

    /**
     * Method info and options for logging its time execution. Arguments are not included by default.
     * @param methodName name of the method or a block of code. Example: private void parent(int a, int b).
     * @param timeThreshold threshold for logging. If method execution time is below the threshold, it won't be logged.
     * @param timeUnit time unit for logging execution time.
     */
    public TimeTrackerInfo(String methodName, int timeThreshold, ChronoUnit timeUnit) {
        this(methodName, TimeTrackerConstant.EMPTY_STRING, false, timeThreshold, timeUnit);
    }

}

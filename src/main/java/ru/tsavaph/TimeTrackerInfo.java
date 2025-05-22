package ru.tsavaph;

import jakarta.validation.constraints.Min;

import java.time.temporal.ChronoUnit;

/**
 * Method info and options for logging its time execution.
 *
 * @param methodName        name of the method or a block of code. Example: private void parent(int a, int b).
 * @param methodArguments   arguments of the method or a block code. Example: a=1, b=2.
 * @param argumentsIncluded include arguments into log or not.
 * @param timeThreshold     threshold for logging. If method execution time is below the threshold, it won't be logged.
 * @param timeUnit          time unit for logging execution time.
 */
public record TimeTrackerInfo(
        String methodName,
        String methodArguments,
        boolean argumentsIncluded,
        @Min(-1) int timeThreshold,
        ChronoUnit timeUnit) {

    /**
     * Method info and options for logging its time execution. Arguments are not included by default.
     *
     * @param methodName    name of the method or a block of code. Example: private void parent(int a, int b).
     * @param timeThreshold threshold for logging. If method execution time is below the threshold, it won't be logged.
     * @param timeUnit      time unit for logging execution time.
     */
    public TimeTrackerInfo(String methodName, @Min(-1) int timeThreshold, ChronoUnit timeUnit) {
        this(methodName, TimeTrackerConstant.EMPTY_STRING, false, timeThreshold, timeUnit);
    }

    /**
     * Method info and options for logging its time execution. Arguments are not included by default. Time units
     * {@link ChronoUnit#MILLIS}.
     *
     * @param methodName    name of the method or a block of code. Example: private void parent(int a, int b).
     * @param timeThreshold threshold for logging. If method execution time is below the threshold, it won't be logged.
     */
    public TimeTrackerInfo(String methodName, @Min(-1) int timeThreshold) {
        this(methodName, TimeTrackerConstant.EMPTY_STRING, false, timeThreshold, ChronoUnit.MILLIS);
    }

    /**
     * Method info and options for logging its time execution. Arguments are not included by default. Time units
     * {@link ChronoUnit#MILLIS}.
     *
     * @param methodName name of the method or a block of code. Example: private void parent(int a, int b).
     */
    public TimeTrackerInfo(String methodName) {
        this(methodName, TimeTrackerConstant.EMPTY_STRING, false, TimeTrackerConstant.NO_TIME_THRESHOLD, ChronoUnit.MILLIS);
    }

    /**
     * Method info and options for logging its time execution. Time units
     * {@link ChronoUnit#MILLIS}.
     *
     * @param methodName      name of the method or a block of code. Example: private void parent(int a, int b).
     * @param methodArguments arguments of the method or a block code. Example: a=1, b=2.
     * @param timeThreshold   threshold for logging. If method execution time is below the threshold, it won't be logged.
     */
    public TimeTrackerInfo(String methodName, String methodArguments, @Min(-1) int timeThreshold) {
        this(methodName, methodArguments, true, timeThreshold, ChronoUnit.MILLIS);
    }

    /**
     * Method info and options for logging its time execution.
     *
     * @param methodName      name of the method or a block of code. Example: private void parent(int a, int b).
     * @param methodArguments arguments of the method or a block code. Example: a=1, b=2.
     * @param timeUnit        time unit for logging execution time.
     */
    public TimeTrackerInfo(String methodName, String methodArguments, ChronoUnit timeUnit) {
        this(methodName, methodArguments, true, TimeTrackerConstant.NO_TIME_THRESHOLD, timeUnit);
    }

}

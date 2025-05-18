package ru.tsavaph.aspect;

import ru.tsavaph.TimeTrackerConstant;
import ru.tsavaph.TimeTrackerService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Used for log method execution time. It supports nested execution measurement: if a method of an injected class is
 * also covered by the annotation, it is logged as part of the parent method (limitation: it must be in the same thread).
 * Example:
 * <pre>
 *  public void parent(int a, int b) - 8008600 ns. Arguments: a=1, b=2
 *   |-- public void child(int c) - 16 µs.
 * </pre>
 * Private method are not supported due to Spring AOP limitations. To include private methods or blocks of code
 * inject bean {@link TimeTrackerService}.
 * <p>
 * To enable or disable logging use property {@code time-tracker.enabled}, where {@code true} - enabled,
 * {@code false} - disabled.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimeTracker {

    /**
     * Time unit for logging execution time. By default, {@code µs}.
     */
    ChronoUnit timeUnit() default ChronoUnit.MILLIS;

    /**
     * Include arguments or not into log. See example for parent method.  By default, {@code false}.
     */
    boolean argumentsIncluded() default false;

    /**
     * Threshold for logging. If method execution time is below the threshold, it won't be logged. By default, disabled.
     */
    int timeThreshold() default TimeTrackerConstant.NO_TIME_THRESHOLD;

}

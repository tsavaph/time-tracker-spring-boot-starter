package ru.tsavaph.aspect;

import ru.tsavaph.TimeTrackerConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimeTracker {

    ChronoUnit timeUnit() default ChronoUnit.MILLIS;
    boolean argumentsIncluded() default false;
    int timeThreshold() default TimeTrackerConstant.NO_TIME_THRESHOLD;

}

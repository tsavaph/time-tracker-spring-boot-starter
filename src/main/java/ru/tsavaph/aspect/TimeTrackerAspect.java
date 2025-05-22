package ru.tsavaph.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import ru.tsavaph.TimeTrackerConstant;
import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Aspect for {@link TimeTracker} annotation. Prepares parameters from annotation and pass it to the
 * {@link TimeTrackerService} to log the method execution time.
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class TimeTrackerAspect {

    private final TimeTrackerService timeTrackerService;
    private final Environment environment;

    /**
     * Tracks execution time around annotated method.
     *
     * @param joinPoint proceeding method.
     * @return the result of the method.
     */
    @Around("@annotation(ru.tsavaph.aspect.TimeTracker)")
    public Object trackTime(ProceedingJoinPoint joinPoint) {
        TimeTracker timeTrackerAnnotation = extractTimeTrackerAnnotation(joinPoint);
        TimeTrackerInfo timeTrackerInfo = new TimeTrackerInfo(
                extractMethodName(joinPoint),
                extractMethodArguments(joinPoint),
                timeTrackerAnnotation.argumentsIncluded(),
                chooseTimeThreshold(timeTrackerAnnotation),
                timeTrackerAnnotation.timeUnit()
        );
        return timeTrackerService.trackTime(timeTrackerInfo, () -> proceed(joinPoint));
    }

    private Object proceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof Error error) {
                throw error;
            }
            if (throwable instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(throwable);
        }
    }

    private int chooseTimeThreshold(TimeTracker timeTrackerAnnotation) {
        String propertyTimeThreshold = timeTrackerAnnotation.propertyTimeThreshold();
        if (TimeTrackerConstant.NO_PROPERTY_THRESHOLD.equals(propertyTimeThreshold)) {
            return timeTrackerAnnotation.timeThreshold();
        }
        return Integer.parseInt(environment.resolvePlaceholders(propertyTimeThreshold));
    }

    private String extractMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = extractMethodSignature(joinPoint);
        Method method = methodSignature.getMethod();

        String modifiers = Modifier.toString(method.getModifiers());
        String returnType = method.getReturnType().getSimpleName();
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();

        String params = Arrays.stream(parameters)
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", "));

        return String.format("%s %s %s(%s)", modifiers, returnType, methodName, params);
    }

    private String extractMethodArguments(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

        if (args == null || paramNames == null) {
            return TimeTrackerConstant.EMPTY_STRING;
        }

        return IntStream.range(0, args.length)
                .mapToObj(i -> String.format("%s=%s", paramNames[i], args[i]))
                .collect(Collectors.joining(", "));
    }

    private MethodSignature extractMethodSignature(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        assert methodSignature != null;
        return methodSignature;
    }

    private TimeTracker extractTimeTrackerAnnotation(JoinPoint joinPoint) {
        MethodSignature methodSignature = extractMethodSignature(joinPoint);
        TimeTracker annotation = methodSignature.getMethod().getAnnotation(TimeTracker.class);
        assert annotation != null;
        return annotation;
    }

}

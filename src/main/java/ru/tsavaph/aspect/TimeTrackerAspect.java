package ru.tsavaph.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import ru.tsavaph.TimeTrackerConstant;
import ru.tsavaph.TimeTrackerInfo;
import ru.tsavaph.TimeTrackerService;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class TimeTrackerAspect {

    private final TimeTrackerService timeTrackerService;

    @Around("@annotation(ru.tsavaph.aspect.TimeTracker)")
    public Object trackTime(ProceedingJoinPoint joinPoint) {
        var timeTrackerAnnotation = extractTimeTrackerAnnotation(joinPoint);
        var timeTrackerInfo = new TimeTrackerInfo(
                extractMethodName(joinPoint),
                extractMethodArguments(joinPoint),
                timeTrackerAnnotation.argumentsIncluded(),
                timeTrackerAnnotation.timeThreshold(),
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


    private String extractMethodName(ProceedingJoinPoint joinPoint) {
        var methodSignature = extractMethodSignature(joinPoint);
        var method = methodSignature.getMethod();

        var modifiers = Modifier.toString(method.getModifiers());
        var returnType = method.getReturnType().getSimpleName();
        var methodName = method.getName();
        var parameters = method.getParameters();

        var params = Arrays.stream(parameters)
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", "));

        return String.format("%s %s %s(%s)", modifiers, returnType, methodName, params);
    }

    private String extractMethodArguments(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        var paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

        if (args == null || paramNames == null) {
            return TimeTrackerConstant.EMPTY_STRING;
        }

        return IntStream.range(0, args.length)
                .mapToObj(i -> String.format("%s=%s", paramNames[i], args[i]))
                .collect(Collectors.joining(", "));
    }

    private MethodSignature extractMethodSignature(JoinPoint joinPoint) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        assert methodSignature != null;
        return methodSignature;
    }

    private TimeTracker extractTimeTrackerAnnotation(JoinPoint joinPoint) {
        var methodSignature = extractMethodSignature(joinPoint);
        var annotation = methodSignature.getMethod().getAnnotation(TimeTracker.class);
        assert annotation != null;
        return annotation;
    }

}

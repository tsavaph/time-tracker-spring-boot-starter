package ru.tsavaph.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.env.Environment;
import ru.tsavaph.TimeTrackerConstant;
import ru.tsavaph.aspect.TimeTracker;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class that validates {@link TimeTracker} annotation on startup.
 */
@RequiredArgsConstructor
public class TimeTrackerAnnotationValidator implements SmartInitializingSingleton {

    private final Pattern pattern = Pattern.compile("^\\$\\{[^}]+}$");

    private final ListableBeanFactory beanFactory;
    private final Environment environment;

    @Override
    public void afterSingletonsInstantiated() {
        List<String> errors = new LinkedList<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(beanFactory.getBean(beanName));
            for (Method method : targetClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(TimeTracker.class)) {
                    TimeTracker annotation = method.getAnnotation(TimeTracker.class);
                    assert annotation != null;

                    validateTimeUnit(method, annotation, errors, targetClass);
                    validateTimeThreshold(method, annotation, errors, targetClass);
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Invalid @TimeTracker annotation: " + String.join(", ", errors));
        }
    }

    private static void validateTimeUnit(Method method, TimeTracker annotation, List<String> errors, Class<?> targetClass) {
        ChronoUnit timeUnit = annotation.timeUnit();
        if (!TimeTrackerConstant.ALLOWED_TIME_UNITS.contains(timeUnit)) {
            errors.add(String.format(
                    "timeUnit on method %s.%s must be from %s",
                    targetClass.getName(),
                    method.getName(),
                    TimeTrackerConstant.ALLOWED_TIME_UNITS
            ));
        }
    }

    private void validateTimeThreshold(Method method, TimeTracker annotation, List<String> errors, Class<?> targetClass) {
        String propertyTimeThreshold = annotation.propertyTimeThreshold();
        if (!TimeTrackerConstant.NO_PROPERTY_THRESHOLD.equals(propertyTimeThreshold)) {
            try {
                if (!pattern.matcher(propertyTimeThreshold).matches()) {
                    throw new IllegalArgumentException();
                }
                int thresholdValue = Integer.parseInt(environment.resolvePlaceholders(propertyTimeThreshold));
                if (thresholdValue != TimeTrackerConstant.NO_TIME_THRESHOLD && thresholdValue < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.add(String.format(
                        "propertyTimeThreshold set in %s property must be int and >= 0 or disabled (%s)",
                        propertyTimeThreshold,
                        TimeTrackerConstant.NO_PROPERTY_THRESHOLD
                ));
            } catch (IllegalArgumentException e) {
                errors.add(String.format(
                        "propertyTimeThreshold on method %s.%s must match pattern '${...}'",
                        method.getDeclaringClass().getName(),
                        method.getName()
                ));
            }
        } else {
            int timeThreshold = annotation.timeThreshold();
            if (timeThreshold != TimeTrackerConstant.NO_TIME_THRESHOLD && timeThreshold < 0) {
                errors.add(String.format(
                        "timeThreshold on method %s.%s must be disabled (%d) or >= 0",
                        targetClass.getName(),
                        method.getName(),
                        TimeTrackerConstant.NO_TIME_THRESHOLD
                ));
            }
        }
    }

}

package ru.tsavaph;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Context for the thread with all included tracking methods or blocks of code execution.
 */
@Getter
@Slf4j
public class TimeTrackerThreadContext {

    private final List<TimeTrackerMethodContext> methodContexts;

    private int pointerDepth;

    public TimeTrackerThreadContext() {
        methodContexts = new ArrayList<>();
        pointerDepth = 0;
    }

    public void increasePointerDepth() {
        pointerDepth++;
    }

    public void decreasePointerDepth() {
        pointerDepth--;
    }

    public void addMethodContext(TimeTrackerMethodContext timeTrackerMethodContext) {
        this.methodContexts.add(timeTrackerMethodContext);
    }

    public void logMethodTimeTrace() {
        if (methodContexts.isEmpty()) {
            return;
        }
        var messageTemplateBuilder = new StringBuilder();
        var infoToBeLogged = new ArrayList<>();

        for (var methodContext : methodContexts) {
            if (methodContext.isTimeThresholdExceeded()) {
                messageTemplateBuilder.append("\n{} {} - {} {}");

                var offset = generateOffset(methodContext.getPointerDepth());
                var methodName = methodContext.getMethodName();
                var executionTime = methodContext.getExecutionTimeInTimeUnit();
                var timeUnit = methodContext.getTimeUnitString();
                infoToBeLogged.add(offset);
                infoToBeLogged.add(methodName);
                infoToBeLogged.add(executionTime);
                infoToBeLogged.add(timeUnit);

                if (methodContext.isArgumentsIncluded()) {
                    messageTemplateBuilder.append(". Arguments: {}");
                    var arguments = methodContext.getArguments();
                    infoToBeLogged.add(arguments);
                }
            }
        }
        log.info(messageTemplateBuilder.toString(), infoToBeLogged.toArray());
    }

    private String generateOffset(int pointerDepth) {
        if (pointerDepth == 0) {
            return TimeTrackerConstant.EMPTY_STRING;
        }
        return TimeTrackerConstant.SPACE.repeat(pointerDepth * 2)
                + TimeTrackerConstant.OFFSET_SYMBOL;
    }

}

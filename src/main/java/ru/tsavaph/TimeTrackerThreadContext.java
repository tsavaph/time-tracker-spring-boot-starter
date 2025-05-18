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

    /**
     * Initialize thread context for logging.
     */
    public TimeTrackerThreadContext() {
        methodContexts = new ArrayList<>();
        pointerDepth = 0;
    }

    /**
     * Increase a pointer showing the current method in queue is being executed.
     */
    public void increasePointerDepth() {
        pointerDepth++;
    }

    /**
     * Decrease a pointer showing the current method in queue is being executed.
     */
    public void decreasePointerDepth() {
        pointerDepth--;
    }

    /**
     * Add executing method context to the thread context.
     *
     * @param timeTrackerMethodContext the context of executing method.
     */
    public void addMethodContext(TimeTrackerMethodContext timeTrackerMethodContext) {
        this.methodContexts.add(timeTrackerMethodContext);
    }

    /**
     * Log all execution time info after completing of the parent method.
     */
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

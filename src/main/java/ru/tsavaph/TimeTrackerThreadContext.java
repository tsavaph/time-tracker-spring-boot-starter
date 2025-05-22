package ru.tsavaph;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

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
    public void logMethodTimeTrace(Level logLevel) {
        if (methodContexts.isEmpty()) {
            return;
        }
        if (!methodContexts.get(0).isTimeThresholdExceeded()) {
            return;
        }
        StringBuilder messageTemplateBuilder = new StringBuilder();
        List<String> infoToBeLogged = new ArrayList<>();

        for (TimeTrackerMethodContext methodContext : methodContexts) {
            if (methodContext.isTimeThresholdExceeded()) {
                messageTemplateBuilder.append("\n{} {} - {} {}");

                String offset = generateOffset(methodContext.getPointerDepth());
                String methodName = methodContext.getTimeTrackerInfo().methodName();
                long executionTime = methodContext.getExecutionTimeInTimeUnit();
                String timeUnit = methodContext.getTimeUnitString();
                infoToBeLogged.add(offset);
                infoToBeLogged.add(methodName);
                infoToBeLogged.add(String.valueOf(executionTime));
                infoToBeLogged.add(timeUnit);

                if (methodContext.getTimeTrackerInfo().argumentsIncluded()) {
                    messageTemplateBuilder.append(". Arguments: {}");
                    String arguments = methodContext.getTimeTrackerInfo().methodArguments();
                    infoToBeLogged.add(arguments);
                }
            }
        }
        logIfNotEmpty(messageTemplateBuilder, infoToBeLogged, logLevel);
    }

    private String generateOffset(int pointerDepth) {
        if (pointerDepth == 0) {
            return TimeTrackerConstant.EMPTY_STRING;
        }
        return TimeTrackerConstant.SPACE.repeat(pointerDepth * 2)
                + TimeTrackerConstant.OFFSET_SYMBOL;
    }

    private void logIfNotEmpty(StringBuilder messageTemplateBuilder, List<String> infoToBeLogged, Level logLevel) {
        if (!infoToBeLogged.isEmpty()) {
            log.atLevel(logLevel).log(messageTemplateBuilder.toString(), infoToBeLogged.toArray());
        }
    }

}

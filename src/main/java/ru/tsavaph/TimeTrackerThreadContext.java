package ru.tsavaph;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

@Getter
@Slf4j
public class TimeTrackerThreadContext {

    private final Instant creationTime;
    private final List<TimeTrackerMethodContext> methodContexts;

    private int pointerDepth;

    public TimeTrackerThreadContext() {
        creationTime = Instant.now();
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
        for (var methodContext : methodContexts) {
            if (methodContext.isTimeThresholdExceeded()) {
                var infoToBeLogged = new ArrayList<>();
                var messageTemplateBuilder = new StringBuilder("{} {} - {} {}");

                var offset = generateOffset(methodContext.getPointerDepth());
                var methodName = methodContext.getMethodName();
                var executionTime = methodContext.getExecutionTimeInTimeUnit();
                var timeUnit = methodContext.getTimeUnitString();

                infoToBeLogged.add(offset);
                infoToBeLogged.add(methodName);
                infoToBeLogged.add(executionTime);
                infoToBeLogged.add(timeUnit);

                if (methodContext.isArgumentsIncluded()) {
                    var arguments = methodContext.getArguments();
                    infoToBeLogged.add(arguments);
                    messageTemplateBuilder.append(". Arguments: {}");
                }

                log.info(messageTemplateBuilder.toString(), infoToBeLogged.toArray());
            }
        }
    }

    private String generateOffset(int pointerDepth) {
        if (pointerDepth == 0) {
            return TimeTrackerConstant.EMPTY_STRING;
        }
        return TimeTrackerConstant.SPACE.repeat(Math.max(0, pointerDepth * 2))
                + TimeTrackerConstant.OFFSET_SYMBOL;
    }

}

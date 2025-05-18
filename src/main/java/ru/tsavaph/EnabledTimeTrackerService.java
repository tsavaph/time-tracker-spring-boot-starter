package ru.tsavaph;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation for {@link TimeTrackerService}.
 */
@Slf4j
@RequiredArgsConstructor
public class EnabledTimeTrackerService implements TimeTrackerService {

    private final ThreadLocal<TimeTrackerThreadContext> threadLocalContext = new ThreadLocal<>();

    public <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier) {
        var methodSignatureString = timeTrackerInfo.methodName();
        var methodArgumentString = timeTrackerInfo.methodArguments();
        var isIncludeArgsInLog = timeTrackerInfo.argumentsIncluded();
        var timeThreshold = timeTrackerInfo.timeThreshold();
        var trackingTimeUnit = timeTrackerInfo.timeUnit();

        var threadContext = initTimeTrackerThreadContextIfNull();

        var methodContext = new TimeTrackerMethodContext(
                threadContext.getPointerDepth(),
                methodSignatureString,
                methodArgumentString,
                isIncludeArgsInLog,
                timeThreshold,
                trackingTimeUnit
        );

        var currentThreadName = Thread.currentThread().getName();

        log.debug(
                "TimeTrackerMethodContext for method {} created in thread {}",
                methodContext.getMethodName(),
                currentThreadName
        );

        threadContext.addMethodContext(methodContext);
        threadContext.increasePointerDepth();

        var startTime = System.nanoTime();
        var result = supplier.get();
        var endTime = System.nanoTime();

        methodContext.setExecutionTimeNanos(endTime - startTime);

        log.debug(
                "TimeTrackerMethodContext for method {} finished in thread {}. Execution time = {} ns",
                methodContext.getMethodName(),
                currentThreadName,
                methodContext.getExecutionTimeNanos()
        );

        threadContext.decreasePointerDepth();

        if (threadContext.getPointerDepth() == 0) {
            threadContext.logMethodTimeTrace();
            threadLocalContext.remove();
            log.debug("TimeTrackerThreadContext for thread {} completed and removed", currentThreadName);
        }

        return result;
    }

    public void trackTime(TimeTrackerInfo timeTrackerInfo, Runnable runnable) {
        trackTime(timeTrackerInfo, () -> {
            runnable.run();
            return null;
        });
    }

    private TimeTrackerThreadContext initTimeTrackerThreadContextIfNull() {
        return Optional.ofNullable(threadLocalContext.get())
                .orElseGet(() -> {
                    var context = new TimeTrackerThreadContext();
                    threadLocalContext.set(context);
                    log.debug(
                            "TimeTrackerThreadContext initiated for thread {}",
                            Thread.currentThread().getName()
                    );
                    return context;
                });
    }

}

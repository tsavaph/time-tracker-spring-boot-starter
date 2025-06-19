package ru.tsavaph;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation for {@link TimeTrackerService}.
 */
@Slf4j
@RequiredArgsConstructor
public class EnabledTimeTrackerService implements TimeTrackerService {

    private final ThreadLocal<TimeTrackerThreadContext> threadLocalContext = new ThreadLocal<>();

    private final Level logLevel;

    public <T> T trackTime(TimeTrackerInfo timeTrackerInfo, Supplier<T> supplier) {
        TimeTrackerThreadContext threadContext = initTimeTrackerThreadContextIfNull();

        TimeTrackerMethodContext methodContext = new TimeTrackerMethodContext(threadContext.getPointerDepth(), timeTrackerInfo);

        String currentThreadName = Thread.currentThread().getName();

        log.debug(
                "TimeTrackerMethodContext for method {} created in thread {}",
                timeTrackerInfo.methodName(),
                currentThreadName
        );

        threadContext.addMethodContext(methodContext);
        threadContext.increasePointerDepth();

        long startTime = System.nanoTime();
        try {
            return supplier.get();
        }
        finally {
            long endTime = System.nanoTime();

            methodContext.setExecutionTimeNanos(endTime - startTime);

            log.debug(
                    "TimeTrackerMethodContext for method {} finished in thread {}. Execution time = {} ns",
                    timeTrackerInfo.methodName(),
                    currentThreadName,
                    methodContext.getExecutionTimeNanos()
            );

            threadContext.decreasePointerDepth();

            if (threadContext.getPointerDepth() == 0) {
                threadContext.logMethodTimeTrace(logLevel);
                threadLocalContext.remove();
                log.debug("TimeTrackerThreadContext for thread {} completed and removed", currentThreadName);
            }
        }
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
                    TimeTrackerThreadContext context = new TimeTrackerThreadContext();
                    threadLocalContext.set(context);
                    log.debug(
                            "TimeTrackerThreadContext initiated for thread {}",
                            Thread.currentThread().getName()
                    );
                    return context;
                });
    }

}

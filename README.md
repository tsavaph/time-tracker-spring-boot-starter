# Time Tracker Spring Boot Starter

Time Tracker Spring Boot Starter is a starter based on Spring AOP for measuring execution time of methods or blocks of 
code. The starter has a tree-based architecture: parent method consists of child method (see example).

### Usage
Annotate a public method you want to measure with `@TimeTracker` annotation: on Spring Context initialization proxy 
will be created. Due to the Spring AOP limitations, the proxy won't work on private methods. To measure private methods 
or blocks of code, just inject `TimeTrackerService` bean and place lambda into its methods. It is disabled by default: 
to enable the starter just simple set property `time-tracker.enabled = true`

### Example
#### Code
```java
@Service
@RequiredArgsConstructor
public class ParentTestService {

    private final TimeTrackerService timeTrackerService;
    private final ChildTestService childTestService;

    @TimeTracker
    public void parentTest() {
        timeTrackerService.trackTime(
                new TimeTrackerInfo(
                        "test block of code",
                        1_000_000,
                        ChronoUnit.NANOS),
                () -> {
                    // some logic 
                }
        );
        // some logic 
        childTestService.childTest("test");
    }

}

@Service
@RequiredArgsConstructor
public class ChildTestService {

    private final TimeTrackerService timeTrackerService;

    @TimeTracker(argumentsIncluded = true, timeUnit = ChronoUnit.NANOS)
    public void childTest(String test) {
        var result = timeTrackerService.trackTime(
                new TimeTrackerInfo(
                        "private int testPrivate(int a)",
                        "a=123",
                        true,
                        TimeTrackerConstant.NO_TIME_THRESHOLD,
                        ChronoUnit.MILLIS),
                () -> testPrivate(123)
        );
        // some logic
    }

    private int testPrivate(int a) {
        // some logic
        return 321;
    }

}
```
#### Log output
```
2025-05-18T20:33:04.822+03:00  INFO 9732 --- [tracker-test] [   scheduling-1] ru.tsavaph.TimeTrackerThreadContext      : 
 public void parentTest() - 2 ms
  |-- test block of code - 700 ns
  |-- public void childTest(String test) - 999300 ns. Arguments: test=test
    |-- private int testPrivate(int a) - 1 ms. Arguments: a=123
```

## Installation

#### Gradle
```groovy
implementation group: 'ru.tsavaph', name: 'time-tracker-spring-boot-starter', version: '1.0.0'
```

#### Maven
```xml
<dependency>
    <groupId>ru.tsavaph</groupId>
    <artifactId>time-tracker-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
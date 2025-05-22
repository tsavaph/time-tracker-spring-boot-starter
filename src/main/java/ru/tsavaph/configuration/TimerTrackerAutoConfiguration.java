package ru.tsavaph.configuration;

import org.slf4j.event.Level;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.tsavaph.aspect.TimeTrackerAspect;
import ru.tsavaph.EnabledTimeTrackerService;
import ru.tsavaph.TimeTrackerService;

/**
 * Configuration class for Time Tracker Spring Boot Starter.
 */
@Configuration
class TimerTrackerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "time-tracker", value = "enabled")
    TimeTrackerService enabledTimeTrackerService(@Value("${time-tracker.log-level: INFO}") Level logLevel) {
        return new EnabledTimeTrackerService(logLevel);
    }

    @Bean
    @ConditionalOnProperty(prefix = "time-tracker", value = "enabled")
    TimeTrackerAspect timeTrackerAspect(TimeTrackerService timeTrackerService, Environment environment) {
        return new TimeTrackerAspect(timeTrackerService, environment);
    }

    @Bean
    @ConditionalOnProperty(prefix = "time-tracker", value = "enabled")
    TimeTrackerAnnotationValidator timeTrackerAnnotationValidator(ListableBeanFactory beanFactory, Environment env) {
        return new TimeTrackerAnnotationValidator(beanFactory, env);
    }

    @Bean
    @ConditionalOnMissingBean
    TimeTrackerService disabledTimeTrackerService() {
        return new DisabledTimeTrackerService();
    }

}

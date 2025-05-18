package ru.tsavaph.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tsavaph.aspect.TimeTrackerAspect;
import ru.tsavaph.EnabledTimeTrackerService;
import ru.tsavaph.TimeTrackerService;

@Configuration
class TimerTrackerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "time-tracker", value = "enabled")
    TimeTrackerService enabledTimeTrackerService() {
        return new EnabledTimeTrackerService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "time-tracker", value = "enabled")
    TimeTrackerAspect timeTrackerAspect(TimeTrackerService timeTrackerService) {
        return new TimeTrackerAspect(timeTrackerService);
    }

    @Bean
    @ConditionalOnMissingBean
    TimeTrackerService disabledTimeTrackerService() {
        return new DisabledTimeTrackerService();
    }

}

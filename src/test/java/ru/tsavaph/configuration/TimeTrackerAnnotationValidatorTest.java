package ru.tsavaph.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;
import ru.tsavaph.aspect.TimeTracker;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link TimeTrackerAnnotationValidator}
 */
@ExtendWith(MockitoExtension.class)
class TimeTrackerAnnotationValidatorTest {

    @Mock
    ListableBeanFactory beanFactory;

    @Mock
    Environment environment;

    @InjectMocks
    TimeTrackerAnnotationValidator validator;

    @Test
    void shouldPassWithValidAnnotation() {
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"validService"});
        when(beanFactory.getBean("validService")).thenReturn(new ValidService());

        assertDoesNotThrow(() -> validator.afterSingletonsInstantiated());
    }

    @Test
    void shouldFailOnInvalidTimeUnit() {
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"invalidTimeUnitService"});
        when(beanFactory.getBean("invalidTimeUnitService")).thenReturn(new InvalidTimeUnitService());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> validator.afterSingletonsInstantiated()
        );

        assertTrue(ex.getMessage().contains("timeUnit on method"));
    }

    @Test
    void shouldFailOnInvalidThreshold() {
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"invalidThresholdService"});
        when(beanFactory.getBean("invalidThresholdService")).thenReturn(new InvalidThresholdService());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> validator.afterSingletonsInstantiated()
        );

        assertTrue(ex.getMessage().contains("timeThreshold on method"));
    }

    @Test
    void shouldFailOnInvalidPropertyPattern() {
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"invalidPropertyPatternService"});
        when(beanFactory.getBean("invalidPropertyPatternService")).thenReturn(new InvalidPropertyPatternService());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> validator.afterSingletonsInstantiated()
        );

        assertTrue(ex.getMessage().contains("must match pattern '${...}'"));
    }

    @Test
    void shouldFailOnInvalidPropertyValue() {
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"invalidPropertyValueService"});
        when(beanFactory.getBean("invalidPropertyValueService")).thenReturn(new InvalidPropertyValueService());
        when(environment.resolvePlaceholders("${threshold}")).thenReturn("-5");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> validator.afterSingletonsInstantiated()
        );

        assertTrue(ex.getMessage().contains("must be int and >= 0"));
    }

    static class ValidService {

        @TimeTracker(timeUnit = ChronoUnit.MICROS, timeThreshold = 10)
        public void validMethod() {

        }

    }

    static class InvalidTimeUnitService {

        @TimeTracker(timeUnit = ChronoUnit.FOREVER)
        public void invalidTimeUnitMethod() {

        }

    }

    static class InvalidThresholdService {

        @TimeTracker(timeThreshold = -5)
        public void invalidThresholdMethod() {

        }

    }

    static class InvalidPropertyPatternService {

        @TimeTracker(propertyTimeThreshold = "not-a-placeholder")
        public void invalidPropertyPatternMethod() {

        }

    }

    static class InvalidPropertyValueService {

        @TimeTracker(propertyTimeThreshold = "${threshold}")
        public void invalidPropertyValueMethod() {

        }

    }

}

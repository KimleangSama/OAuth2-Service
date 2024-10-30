package com.kkimleang.authservice.config;

import io.micrometer.observation.*;
import io.micrometer.observation.aop.*;
import org.springframework.context.annotation.*;

@Configuration
public class ObservabilityConfig {
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}

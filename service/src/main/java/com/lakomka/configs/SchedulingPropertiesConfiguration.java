package com.lakomka.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@Slf4j
public class SchedulingPropertiesConfiguration {

    @Value("${app.scheduling.cron:0 */5 * * * *}")
    private String cronExpression;

    @PostConstruct
    void init() {
        log.info("SchedulingProperties: cronExpression={}", cronExpression);
    }

    @Bean
    public SchedulingProperties schedulingProperties(SchedulingPropertiesConfiguration configuration) {
        return new SchedulingProperties(configuration);
    }

    @Getter
    private static class SchedulingProperties {
        private final String cronExpression;

        public SchedulingProperties(SchedulingPropertiesConfiguration configuration) {
            this.cronExpression = configuration.getCronExpression();
        }
    }

}

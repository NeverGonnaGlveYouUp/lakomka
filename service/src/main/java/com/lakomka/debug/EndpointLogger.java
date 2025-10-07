package com.lakomka.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@Component
@Profile("debug")
public class EndpointLogger implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("=== All REST Endpoints ===");

        requestMappingHandlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            if (mapping.getPatternValues().stream()
                    .anyMatch(pattern -> pattern.contains("/"))) {
                log.info("Endpoint: {} {} -> {}",
                        mapping.getMethodsCondition(),
                        mapping.getPatternValues(),
                        method.getMethod());
            }
        });
    }
}
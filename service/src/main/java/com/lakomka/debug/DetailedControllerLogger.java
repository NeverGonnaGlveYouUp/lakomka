package com.lakomka.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Component
@Profile("debug")
public class DetailedControllerLogger implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        printAllControllersWithDetails();
    }

    private void printAllControllersWithDetails() {
        log.info("=== Detailed Controller Information ===");

        // REST Controllers
        context.getBeansWithAnnotation(RestController.class).forEach((name, bean) -> {
            printControllerDetails(name, bean, "REST");
        });

        // Regular Controllers
        context.getBeansWithAnnotation(Controller.class).forEach((name, bean) -> {
            printControllerDetails(name, bean, "MVC");
        });
    }

    private void printControllerDetails(String beanName, Object bean, String type) {
        Class<?> beanClass = bean.getClass();
        log.info(type + " Controller: " + beanName);
        log.info("Class: " + beanClass.getName());

        // Print class-level request mapping
        RequestMapping classMapping = beanClass.getAnnotation(RequestMapping.class);
        if (classMapping != null && classMapping.value().length > 0) {
            log.info("Base path: " + Arrays.toString(classMapping.value()));
        }

        // Print method-level mappings
        log.info("Endpoint methods:");
        for (Method method : beanClass.getDeclaredMethods()) {
            Arrays.stream(method.getAnnotations())
                    .filter(annotation -> annotation.annotationType().getSimpleName().contains("Mapping"))
                    .forEach(annotation -> {
                        log.info("  - " + method.getName() + ": "
                        + annotation.toString().replace("org.springframework.web.bind.annotation.",""));
                    });
        }
        log.info("");
    }
}
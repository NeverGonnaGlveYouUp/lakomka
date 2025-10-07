package com.lakomka.configs;

import com.lakomka.debug.JSessionIdLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("debug")
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JSessionIdLoggingFilter> loggingFilter() {
        log.info("Зарегистрирован фильтр логирования JSESSIONID");
        FilterRegistrationBean<JSessionIdLoggingFilter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new JSessionIdLoggingFilter());
        registrationBean.addUrlPatterns("/*"); // Фильтруем все запросы
        return registrationBean;
    }
}

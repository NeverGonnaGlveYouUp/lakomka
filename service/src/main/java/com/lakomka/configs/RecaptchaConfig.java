package com.lakomka.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
@Getter
public class RecaptchaConfig {

    @Value("${recaptcha.secret.key}")
    private String recaptchaSecretKey;

    @Value("${recaptcha.verify.url:https://www.google.com/recaptcha/api/siteverify}")
    private String recaptchaVerifyUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
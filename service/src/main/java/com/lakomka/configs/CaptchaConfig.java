package com.lakomka.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
@Getter
public class CaptchaConfig {

    @Value("${captcha.secret.key}")
    private String captchaSecretKey;

    @Value("${captcha.default.score:0.5}")
    private Double captchaDefaultScore;

    @Value("${captcha.verify.url:://smartcaptcha.cloud.yandex.ru/validate}")
    private String captchaVerifyUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
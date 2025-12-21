package com.lakomka.services;

import com.lakomka.configs.CaptchaConfig;
import com.lakomka.dto.CaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.springframework.util.StringUtils.hasLength;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaConfig captchaConfig;
    private final RestTemplate restTemplate;

    public boolean unverifyCaptcha(String captchaToken) {

        if (!hasLength(captchaToken)) {
            log.error("Captcha validation fail due token is null");
            return true;
        }

        CaptchaResponse response = pullYandexCloud(captchaToken);

        log.info("Captcha validation: status: {}, message: {}, host: {}",
                response.getStatus(),
                response.getMessage(),
                response.getHost());
        return !Objects.equals(response.getStatus(), "ok");
    }

    private CaptchaResponse pullYandexCloud(String captchaToken) {
        String url = captchaConfig.getCaptchaVerifyUrl();
        String secret = captchaConfig.getCaptchaSecretKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secret);
        map.add("token", captchaToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(url, entity, CaptchaResponse.class);
    }
}
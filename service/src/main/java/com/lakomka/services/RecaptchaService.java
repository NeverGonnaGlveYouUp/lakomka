package com.lakomka.services;

import com.lakomka.configs.RecaptchaConfig;
import com.lakomka.dto.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasLength;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptchaService {

    private final RecaptchaConfig recaptchaConfig;
    private final RestTemplate restTemplate;

    public boolean verifyRecaptcha(String recaptchaToken, String action) {
        try {

            if (!hasLength(recaptchaToken) || !hasLength(action)) {
                log.error("Recaptcha validation fail due token or action is null");
                return false;
            }

            RecaptchaResponse response = pullGoogle(recaptchaToken);

            if (response != null && response.isSuccess()) {
                log.info("Recaptcha validation success with score: {} for action: {}",
                        response.getScore(), response.getAction());
                return response.getScore() > recaptchaConfig.getRecaptchaDefaultScore()
                        && action.equals(response.getAction());
            }

            log.warn("Recaptcha validation fail with score: {}, expected action: {}, returned action: {}",
                    Optional.ofNullable(response).map(RecaptchaResponse::getScore).map(Object::toString).orElse("NULL"),
                    action,
                    Optional.ofNullable(response).map(RecaptchaResponse::getAction).orElse("NULL")
            );
            return false;
        } catch (Exception e) {
            log.error("Recaptcha validation fail with exception message: {}", e.getMessage());
            return false;
        }
    }

    public RecaptchaResponse verifyRecaptchaWithDetails(String recaptchaToken) {
        try {
            if (!hasLength(recaptchaToken)) {
                log.error("Recaptcha validation fail due token is null");
                return null;
            }
            return pullGoogle(recaptchaToken);
        } catch (Exception e) {
            log.error("Recaptcha validation fail with exception message: {}.", e.getMessage());
            return null;
        }
    }

    private RecaptchaResponse pullGoogle(String recaptchaToken) {
        String url = recaptchaConfig.getRecaptchaVerifyUrl();
        String secret = recaptchaConfig.getRecaptchaSecretKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secret);
        map.add("response", recaptchaToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(url, entity, RecaptchaResponse.class);
    }
}
package com.lakomka.services;

import com.lakomka.configs.RecaptchaConfig;
import com.lakomka.dto.RecaptchaResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private final RecaptchaConfig recaptchaConfig;
    private final RestTemplate restTemplate;

    public RecaptchaService(RecaptchaConfig recaptchaConfig, RestTemplate restTemplate) {
        this.recaptchaConfig = recaptchaConfig;
        this.restTemplate = restTemplate;
    }

    public boolean verifyRecaptcha(String recaptchaToken, String action) {
        try {
            String url = recaptchaConfig.getRecaptchaVerifyUrl();
            String secret = recaptchaConfig.getRecaptchaSecretKey();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", secret);
            map.add("response", recaptchaToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            RecaptchaResponse response = restTemplate.postForObject(url, entity, RecaptchaResponse.class);

            if (response != null && response.isSuccess()) {
                // Verify action and score (typically require score > 0.5)
                return response.getScore() > 0.5 && action.equals(response.getAction());
            }

            return false;
        } catch (Exception e) {
            // Log error
            return false;
        }
    }

    public RecaptchaResponse verifyRecaptchaWithDetails(String recaptchaToken) {
        try {
            String url = recaptchaConfig.getRecaptchaVerifyUrl();
            String secret = recaptchaConfig.getRecaptchaSecretKey();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", secret);
            map.add("response", recaptchaToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            return restTemplate.postForObject(url, entity, RecaptchaResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}
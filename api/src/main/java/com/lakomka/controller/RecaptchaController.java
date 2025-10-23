package com.lakomka.controller;

import com.lakomka.dto.RecaptchaRequest;
import com.lakomka.dto.RecaptchaResponse;
import com.lakomka.services.RecaptchaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recaptcha")
public class RecaptchaController {

    private final RecaptchaService recaptchaService;

    public RecaptchaController(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyRecaptcha(@RequestBody RecaptchaRequest request) {
        boolean isValid = recaptchaService.verifyRecaptcha(request.getRecaptchaToken(), "submit");

        if (isValid) {
            return ResponseEntity.ok().body(Map.of("success", true, "message", "reCAPTCHA verification successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "reCAPTCHA verification failed"));
        }
    }

    @PostMapping("/verify-detailed")
    public ResponseEntity<?> verifyRecaptchaDetailed(@RequestBody RecaptchaRequest request) {
        RecaptchaResponse response = recaptchaService.verifyRecaptchaWithDetails(request.getRecaptchaToken());

        if (response != null && response.isSuccess() && response.getScore() > 0.5) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "score", response.getScore(),
                    "action", response.getAction(),
                    "message", "reCAPTCHA verification successful"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "score", response != null ? response.getScore() : 0,
                    "message", "reCAPTCHA verification failed"
            ));
        }
    }
}
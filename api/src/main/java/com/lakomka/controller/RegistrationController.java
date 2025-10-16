package com.lakomka.controller;

import com.lakomka.dto.RegistrationDto;
import com.lakomka.validators.RegistrationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationValidator validator;

    @PostMapping("/api/register/validate")
    public ResponseEntity<Map<String, Object>> validate(
            @Valid @RequestBody RegistrationDto registrationDto,
            BindingResult bindingResult) {

        // Полная валидация ДТО
        // validator.validate(registrationDto, bindingResult);

        // Только валидация реквизитов
        validator.validateRequisitesOnly(registrationDto, bindingResult);

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "Ошибка валидации");
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "Данные валидны");
        response.put("organizationType", registrationDto.getOrganizationType().toString());
        response.put("data", registrationDto);

        return ResponseEntity.ok(response);
    }

}

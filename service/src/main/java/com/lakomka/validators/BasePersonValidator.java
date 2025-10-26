package com.lakomka.validators;

import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class BasePersonValidator implements Validator {

    private final static Integer minLoginLength = 8;
    private final static Integer minPasswordLength = 8;

    private final BasePersonRepository basePersonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationDto.class.equals(clazz) || AuthenticationRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "login.invalid.empty", "Логин обязателен.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.invalid.empty", "Пароль обязателен.");

        BasePerson person;
        if (target instanceof RegistrationDto registrationDto) {
            /// Случай регистрации
            if (registrationDto.getPassword().length() < minPasswordLength) {
                errors.rejectValue("password", "password.invalid.short", MessageFormat.format("Пароль должен быть не менее {0} символов.", minPasswordLength));
            }
            if (!registrationDto.getPassword().equals(registrationDto.getRepeatPassword())) {
                errors.rejectValue("repeatPassword", "password.repeatPassword.not_same", "Пароли должны совпадать.");
            }
            person = new BasePerson(passwordEncoder, registrationDto);
            if (basePersonRepository.findByLogin(registrationDto.getLogin()).isPresent()) {
                errors.rejectValue("login", "login.invalid.taken", "Этот логин занят другим пользователем.");
            }
        } else if (target instanceof AuthenticationRequest authenticationRequest) {
            /// Случай аутентификации
            person = new BasePerson(passwordEncoder, authenticationRequest);
            // нужна ли проверка длинны пароля при аутентификации?
            if (authenticationRequest.getPassword().length() < minPasswordLength) {
                errors.rejectValue("password", "password.invalid.short", MessageFormat.format("Пароль должен быть не менее {0} символов.", minPasswordLength));
            }
        } else throw new RuntimeException("Ошибка при определении регистрации/авторизации в BasePersonValidator");
        // нужна ли проверка длинны логина при аутентификации?
        if (person.getLogin().length() < minLoginLength) {
            errors.rejectValue("login", "login.invalid.short", MessageFormat.format("Логин должен быть не менее {0} символов.", minLoginLength));
        }

    }
}
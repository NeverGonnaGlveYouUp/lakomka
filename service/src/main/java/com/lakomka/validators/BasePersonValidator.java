package com.lakomka.validators;

import com.lakomka.models.person.BasePerson;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.MessageFormat;

@Component("beforeCreateBasePersonValidator")
public class BasePersonValidator implements Validator {

    private Integer minLoginLength = 8;

    private Integer minPasswordLength = 8;

    @Override
    public boolean supports(Class<?> clazz) {
        return BasePerson.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "Логин обязателен.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Пароль обязателен.");

        BasePerson person = (BasePerson) target;
        if (person.getLogin().length() < minLoginLength) {
            errors.rejectValue("login", MessageFormat.format("Логин должен быть не менее ${0} символов.", minLoginLength));
        }
        if (person.getPassword().length() < minPasswordLength) {
            errors.rejectValue("password", MessageFormat.format("Пароль должен быть не менее {0} символов.", minPasswordLength));
        }
        if (!person.getPassword().equals(person.getRepeatPassword())){
            errors.rejectValue("repeatPassword", "Пароли должны совпадать.");
        }
    }
}
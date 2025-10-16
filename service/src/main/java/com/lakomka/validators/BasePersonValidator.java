package com.lakomka.validators;

import com.lakomka.dto.security.AuthenticationRequest;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.MessageFormat;

@Component("beforeCreateBasePersonValidator")
public class BasePersonValidator implements Validator {

    private final static Integer minLoginLength = 8;
    private final static Integer minPasswordLength = 8;

    @Autowired
    private BasePersonRepository basePersonRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return BasePerson.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "Логин обязателен.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Пароль обязателен.");

        BasePerson person;
        if (target instanceof BasePerson){
            /// Случай регистрации
            person = (BasePerson) target;
            if(basePersonRepository.findByLogin(person.getLogin()).isPresent()){
                errors.rejectValue("login", "Этот логин занят другим пользователем.");
            }
        } else if (target instanceof AuthenticationRequest) {
            /// Случай авторизации
            person = ((AuthenticationRequest) target).createBasePerson();
        } else throw new RuntimeException();

        if (person.getLogin().length() < minLoginLength) {
            errors.rejectValue("login", MessageFormat.format("Логин должен быть не менее {0} символов.", minLoginLength));
        }
        if (person.getPassword().length() < minPasswordLength) {
            errors.rejectValue("password", MessageFormat.format("Пароль должен быть не менее {0} символов.", minPasswordLength));
        }
        if (!person.getPassword().equals(person.getRepeatPassword()) && person.getRepeatPassword() != null){
            errors.rejectValue("repeatPassword", "Пароли должны совпадать.");
        }
    }
}
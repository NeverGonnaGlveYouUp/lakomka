package com.lakomka.controller;

import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.dtoAssemblers.RegistrationDtoAssembler;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.person.Person;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.utils.JwtUtil;
import com.lakomka.utils.ReCaptchaV3Util;
import com.lakomka.validators.BasePersonValidator;
import com.lakomka.validators.RegistrationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BasePersonRepository basePersonRepository;
    private final JPersonRepository jPersonRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BasePersonValidator basePersonValidator;
    private final RegistrationValidator registrationValidator;
    private final RegistrationDtoAssembler registrationDtoAssembler;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(
            @Valid @RequestBody RegistrationDto user
    ) throws IOException {

        Errors errors = new BeanPropertyBindingResult(user, "user");
        user.setInn(user.getInn().replaceAll("-", ""));
        basePersonValidator.validate(user, errors);
        registrationValidator.validateRequisitesOnly(user, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        ResponseEntity<String> recaptcha_validation_failed = ReCaptchaV3Util.validate(user);
        if (recaptcha_validation_failed != null) {
            return recaptcha_validation_failed;
        }

        Object personType = registrationDtoAssembler.toEntity(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        BasePerson basePerson = new BasePerson(user);
        log.debug("Signup: BasePerson: {}", basePerson.getLogin());
        if (personType instanceof JPerson jPerson) {
            basePerson.setjPerson(jPerson);
            jPerson.setBasePerson(basePerson);
            basePerson = basePersonRepository.save(basePerson);
            jPerson.setBasePerson(basePerson);
            jPersonRepository.save(jPerson);
            log.debug("Signup: JPerson: {}", jPerson.getName());
        } else if (personType instanceof Person person) {
            ///todo: сейчас поддержки ФЛ нет
            throw new UnsupportedOperationException("Поддержка ФЛ не реализована");
        } else throw new RuntimeException("Неопознанный тип пользователя");

        authUser(basePerson.getLogin(), basePerson.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Connection", "close")
                .body(new Token(jwtUtil.generateToken(basePerson.getLogin()), "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        Errors errors = new BeanPropertyBindingResult(authenticationRequest, "user");
        basePersonValidator.validate(authenticationRequest, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        authUser(authenticationRequest.getLogin(), authenticationRequest.getPassword());

        log.debug("Login: {}", authenticationRequest.getLogin());
        return ResponseEntity.status(HttpStatus.OK)
                .header("Connection", "close")
                .body(new Token(jwtUtil.generateToken(authenticationRequest.getLogin()), "Bearer"));
    }

    private void authUser(String login, String password) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }

    private record Token(String token, String type) {
    }
}

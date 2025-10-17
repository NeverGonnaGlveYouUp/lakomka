package com.lakomka.controller;

import com.lakomka.dto.RegistrationDto;
import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dtoAssemblers.RegistrationDtoAssembler;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.person.Person;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.services.CustomUserDetailsService;
import com.lakomka.utils.JwtUtil;
import com.lakomka.validators.BasePersonValidator;
import com.lakomka.validators.RegistrationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private BasePersonRepository basePersonRepository;

    @Autowired
    private JPersonRepository jPersonRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BasePersonValidator basePersonValidator;

    @Autowired
    private RegistrationValidator registrationValidator;

    @Autowired
    private RegistrationDtoAssembler registrationDtoAssembler;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(
            @Valid @RequestBody RegistrationDto user
    ) {
        Errors errors = new BeanPropertyBindingResult(user, "user");
        user.setInn(user.getInn().replaceAll("-", ""));
        basePersonValidator.validate(user, errors);
        registrationValidator.validateRequisitesOnly(user, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        Object personType = registrationDtoAssembler.toEntity(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        BasePerson basePerson = new BasePerson(user);
        if(personType instanceof JPerson jPerson){
            basePerson.setjPerson(jPerson);
            jPerson.setBasePerson(basePerson);
            basePerson = basePersonRepository.save(basePerson);
            jPerson.setBasePerson(basePerson);
            jPersonRepository.save(jPerson);
        } else if (personType instanceof Person person) {
            ///todo: сейчас поддержки ФЛ нет
        } else throw new RuntimeException("Неопознанный тип пользователя");

        final UserDetails userDetails = userDetailsService.loadUserByUsername(basePerson.getLogin());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Connection", "close")
                .body(new Token(jwt, "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody AuthenticationRequest authenticationRequest
    ) throws Exception {
        Errors errors = new BeanPropertyBindingResult(authenticationRequest, "user");
        basePersonValidator.validate(authenticationRequest, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getLogin(),
                authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getLogin());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Connection", "close")
                .body(new Token(jwt, "Bearer"));
    }

    private record Token(String token, String type) {}
}

package com.lakomka.controller;

import com.lakomka.dto.security.AuthenticationRequest;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.services.CustomUserDetailsService;
import com.lakomka.utils.JwtUtil;
import com.lakomka.validators.BasePersonValidator;
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
    private BasePersonRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BasePersonValidator basePersonValidator;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody BasePerson user) {
        Errors errors = new BeanPropertyBindingResult(user, "user");
        basePersonValidator.validate(user, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
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

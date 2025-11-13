package com.lakomka.controller;

import com.lakomka.dto.AuthenticationRequest;
import com.lakomka.dto.ChangePasswordRequest;
import com.lakomka.dto.LoggedUser;
import com.lakomka.dto.RegistrationDto;
import com.lakomka.dtoAssemblers.RegistrationDtoAssembler;
import com.lakomka.models.misc.Route;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.JPerson;
import com.lakomka.models.person.Person;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.person.JPersonRepository;
import com.lakomka.services.RecaptchaService;
import com.lakomka.services.cart.CartService;
import com.lakomka.utils.JwtUtil;
import com.lakomka.validators.BasePersonValidator;
import com.lakomka.validators.RegistrationValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
    private final RecaptchaService recaptchaService;
    private final CartService cartService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(
            @Valid @RequestBody RegistrationDto user,
            HttpServletRequest request
    ) {
        Errors errors = new BeanPropertyBindingResult(user, "user");
        user.setInn(user.getInn().replaceAll("-", ""));
        basePersonValidator.validate(user, errors);
        registrationValidator.validateRequisitesOnly(user, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        if (recaptchaService.verifyRecaptcha(user.getToken(), user.getSiteKey())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Object personType = registrationDtoAssembler.toEntity(user);

        BasePerson basePerson = new BasePerson(passwordEncoder, user);
        log.info("Signup: BasePerson: {}", basePerson.getLogin());
        if (personType instanceof JPerson jPerson) {
            basePerson.setjPerson(jPerson);
            jPerson.setBasePerson(basePerson);
            basePerson = basePersonRepository.save(basePerson);
            jPerson.setBasePerson(basePerson);
            jPersonRepository.save(jPerson);
            log.info("Signup: JPerson: {}", jPerson.getName());
        } else if (personType instanceof Person person) {
            ///todo: сейчас поддержки ФЛ нет
            throw new UnsupportedOperationException("Поддержка ФЛ не реализована");
        } else throw new RuntimeException("Неопознанный тип пользователя");

        authUser(user.getLogin(), user.getPassword());

        cartService.moveGuestCartToUserCart(basePerson, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Connection", "close")
                .body(new Token(jwtUtil.generateToken(basePerson.getLogin()), "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletRequest request
    ) {
        Errors errors = new BeanPropertyBindingResult(authenticationRequest, "user");
        basePersonValidator.validate(authenticationRequest, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        if (recaptchaService.verifyRecaptcha(authenticationRequest.getToken(), authenticationRequest.getSiteKey())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        authUser(authenticationRequest.getLogin(), authenticationRequest.getPassword());

        // Переносим товары из анонимной корзины в пользовательскую
        BasePerson authenticatedUser = basePersonRepository.findByLogin(authenticationRequest.getLogin())
                .orElseThrow(() -> new RuntimeException("Пользователь " + authenticationRequest.getLogin() + " не найден"));
        cartService.moveGuestCartToUserCart(authenticatedUser, request);

        log.info("Login: {}", authenticationRequest.getLogin());
        return ResponseEntity.status(HttpStatus.OK)
                .header("Connection", "close")
                .body(new Token(jwtUtil.generateToken(authenticationRequest.getLogin()), "Bearer"));
    }

    @GetMapping("/current-user/model")
    public ResponseEntity<?> getUserModel(
            @AuthenticationPrincipal BasePerson user
    ) {
        if (Optional.ofNullable(user).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<JPerson> optionalJPerson = jPersonRepository.findById(user.getId());
        if (optionalJPerson.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        JPerson jPerson = optionalJPerson.get();
        LoggedUser personDto = LoggedUser.builder()
                .userName(user.getLogin())
                .name(jPerson.getName())
                .nameFull(jPerson.getNameFull())
                .address(jPerson.getAddress())
                .OGRN(jPerson.getOGRN())
                .INN(jPerson.getINN())
                .KPP(jPerson.getKPP())
                .phone(jPerson.getPhone())
                .email(jPerson.getEmail())
                .contact(jPerson.getContact())
                .post(jPerson.getPost())
                .addressDelivery(jPerson.getAddressDelivery())
                .mapDelivery(jPerson.getMapDelivery())
                .rest(jPerson.getRest())
                .restTime(jPerson.getRestTime())
                .route(Optional.ofNullable(jPerson.getRoute()).orElseGet(Route::new).getRouteString())
                .build();
        log.info(personDto.toString());
        return ResponseEntity.ok().body(personDto);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal BasePerson user
    ) {
        return ResponseEntity.ok()
                .body(
                        LoggedUser.builder()
                                .userName(Optional.ofNullable(user).map(BasePerson::getLogin).orElse("Anonymous"))
                                .build()
                );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal BasePerson user,
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {

        // check authenticated access to this endpoint
        if (isNull(user)) {
            log.warn("Attempt UNAUTHORIZED Password change");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Begin change password for: {}", user.getUsername());

        if (recaptchaService.verifyRecaptcha(changePasswordRequest.getToken(), changePasswordRequest.getSiteKey())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get current authenticated user from context, not from dto!
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Validate request
        Errors errors = new BeanPropertyBindingResult(changePasswordRequest, "changePasswordRequest");
        if (!StringUtils.hasLength(changePasswordRequest.getCurrentPassword())) {
            errors.rejectValue("currentPassword", "currentPassword.empty", "Старый пароль не может быть пустым");
        }
        if (!StringUtils.hasLength(changePasswordRequest.getNewPassword())) {
            errors.rejectValue("newPassword", "newPassword.empty", "Новый пароль не может быть пустым");
        }
        if (!StringUtils.hasLength(changePasswordRequest.getNewPasswordRepeat())) {
            errors.rejectValue("newPasswordRepeat", "newPasswordRepeat.empty", "Повтор нового пароля не может быть пустым");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getNewPasswordRepeat())) {
            errors.rejectValue("newPasswordRepeat", "newPasswordRepeat.equals.newPassword", "Новый пароль не совпадает с повторением нового пароля");
        }
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getCurrentPassword())) {
            errors.rejectValue("newPassword", "newPassword.equals.oldPassword", "Новый пароль не может совпадать со старым");
        }
        if (errors.hasErrors()) {
            log.warn("Attempt invalidated Password change for user {}", currentUsername);
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        BasePerson basePerson = basePersonRepository.findByLogin(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь " + currentUsername + "не найден"));

        // Check if old password is correct
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), basePerson.getPassword())) {
            return ResponseEntity.badRequest().body("Неверный старый пароль");
        }

        // Encode and update new password
        basePerson.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        basePersonRepository.save(basePerson);

        log.info("Success Password changed for user: {}", currentUsername);
        return ResponseEntity.ok().body(new Token(jwtUtil.generateToken(basePerson.getUsername()), "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {

        if (nonNull(user)) {
            log.info("Logout: {}", user.getLogin());
        }

        // Manually invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Return success response
        return ResponseEntity.ok().build();
    }

    private void authUser(String login, String password) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }

    private record Token(String token, String type) {
    }
}
package com.lakomka.configs;

import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Order(-1)
@Slf4j
@RequiredArgsConstructor
public class SystemUserDatabaseInitializer implements CommandLineRunner {

    public static final String SYSTEM_USER = "SystemUser";

    private final BasePersonRepository basePersonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        final String ASCII_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-=+@#$%*&";
        final Random random = new Random(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        Optional<BasePerson> optionalBasePerson = basePersonRepository.findByLogin(SYSTEM_USER);
        if (optionalBasePerson.isPresent()) {
            log.info("System user id={}", optionalBasePerson.get().getId());
            return;
        }

        BasePerson person = new BasePerson();
        person.setLogin(SYSTEM_USER);
        person.setPassword(passwordEncoder.encode(
                IntStream.range(0, 40)
                        .mapToObj(i -> String.valueOf(ASCII_CHARS.charAt(random.nextInt(ASCII_CHARS.length()))))
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString()
        ));

        BasePerson saved = basePersonRepository.save(person);

        log.info("Generate system user id={}", saved.getId());

    }
}
package com.lakomka.shell;

import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.person.BasePersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShellUserService {

    private final PasswordEncoder passwordEncoder;
    private final BasePersonRepository basePersonRepository;

    public String resetPassword(Long userId) {
        BasePerson user = basePersonRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        String newPassword = ShellPasswordGenerator.generateRandomPassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        basePersonRepository.save(user);

        //todo отправить пароль юзеру, если у сайта появится почта

        return newPassword;
    }
}

package com.lakomka.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class UserShell {

    private final ShellUserService shellUserService;

    @ShellMethod(
            value = "Поменять пароль пользователя по id, на рандомный.",
            key = "reset-user-password")
    public void resetUserPassword(
            @ShellOption(value = {"--id", "-i"}) Long userId
    ) {
        log.info("Новый пароль: {}", shellUserService.resetPassword(userId));
    }
}

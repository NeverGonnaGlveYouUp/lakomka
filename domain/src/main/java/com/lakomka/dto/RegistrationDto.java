package com.lakomka.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private String login;
    private String password;
    private String repeatPassword;
    private String token;
    private String expectedAction;
    private String siteKey;
}
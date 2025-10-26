package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String newPasswordRepeat;
    private String siteKey;
    private String token;
    private String expectedAction;
}

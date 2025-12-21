package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String status;
    private String message;
    private String host;
}
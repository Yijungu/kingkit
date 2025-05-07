package com.kingkit.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReissueRequestDto {

    @NotBlank
    private String refreshToken;
}

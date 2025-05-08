package com.kingkit.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor          // ⚠️ 반드시 필요
public class ReissueRequestDto {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    @JsonCreator            // (선택) 명시적 생성자 사용 시
    public ReissueRequestDto(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

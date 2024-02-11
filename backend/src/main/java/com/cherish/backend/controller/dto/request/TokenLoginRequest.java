package com.cherish.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenLoginRequest {

    @NotBlank
    private String token;

    public TokenLoginRequest(String token) {
        this.token = token;
    }
}

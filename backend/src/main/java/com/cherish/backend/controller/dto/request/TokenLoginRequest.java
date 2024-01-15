package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenLoginRequest {

    private String token;

    public TokenLoginRequest(String token) {
        this.token = token;
    }
}

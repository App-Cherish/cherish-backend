package com.cherish.backend.controller.dto.response;

import lombok.Getter;

@Getter
public class AnotherPlatformResponse {

    String message;

    LoginResponse loginResponse;

    public AnotherPlatformResponse(String message, LoginResponse loginResponse) {
        this.message = message;
        this.loginResponse = loginResponse;
    }
}

package com.cherish.backend.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnotherPlatformResponse {

    String message;

    public AnotherPlatformResponse(String message) {
        this.message = message;
    }
}

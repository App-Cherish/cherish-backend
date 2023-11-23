package com.cherish.backend.controller.dto.response;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommonValidationError {

    private final String code;
    private final String message;
    private final Map<String, String> validation = new HashMap();

    public CommonValidationError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addValidation(String field, String defaultMessage) {
        this.validation.put(field, defaultMessage);
    }
}

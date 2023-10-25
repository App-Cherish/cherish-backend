package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.response.CommonErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonErrorResponse notBlankRequestHandler(BindException e) {

        CommonErrorResponse errorResponse = new CommonErrorResponse("400", "잘못된 요청 입니다.");

        for (FieldError error : e.getFieldErrors()) {
            errorResponse.addValidation(error.getField(), error.getDefaultMessage());
        }

        return errorResponse;
    }


}

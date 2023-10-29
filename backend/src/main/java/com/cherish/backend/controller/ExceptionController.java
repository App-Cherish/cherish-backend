package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.response.CommonErrorResponse;
import com.cherish.backend.controller.dto.response.CommonValidationError;
import com.cherish.backend.exception.ExistOauthIdException;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.exception.NotFountTokenException;
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
    public CommonValidationError notBlankRequestHandler(BindException e) {

        CommonValidationError errorResponse = new CommonValidationError("400", "잘못된 요청 입니다.");

        for (FieldError error : e.getFieldErrors()) {
            errorResponse.addValidation(error.getField(), error.getDefaultMessage());
        }

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public CommonErrorResponse commonExceptionHandler(Exception e) {
        return new CommonErrorResponse("400", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotExistAccountException.class)
    public CommonErrorResponse notExistAccountExceptionHandler(Exception e) {
        return new CommonErrorResponse("404", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFountTokenException.class)
    public CommonErrorResponse notFountTokenExceptionHandler(Exception e) {
        return new CommonErrorResponse("404", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExistOauthIdException.class)
    public CommonErrorResponse existOauthIdExceptionHandler(Exception e) {
        return new CommonErrorResponse("400", e.getMessage());
    }

}

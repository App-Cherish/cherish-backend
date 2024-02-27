package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.response.CommonErrorResponse;
import com.cherish.backend.controller.dto.response.CommonValidationError;
import com.cherish.backend.exception.base.*;
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
    public CommonValidationError validationExceptionHandler(BindException e) {
        CommonValidationError errorResponse = new CommonValidationError("400", "validation 오류 입니다.");

        for (FieldError error : e.getFieldErrors()) {
            errorResponse.addValidation(error.getField(), error.getDefaultMessage());
        }

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExistBaseException.class)
    public CommonErrorResponse existBaseExceptionHandler(Exception e) {
        return new CommonErrorResponse("400", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ConvertTypeBaseException.class)
    public CommonErrorResponse notConvertTypeException(Exception e) {
        return new CommonErrorResponse("400", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenAccessBaseException.class)
    public CommonErrorResponse forbiddenAccessBaseException(Exception e) {
        return new CommonErrorResponse("403", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotExistBaseException.class)
    public CommonErrorResponse notExistBaseExceptionHandler(Exception e) {
        return new CommonErrorResponse("404", e.getMessage());
    }

    @ResponseStatus(HttpStatus.MULTIPLE_CHOICES)
    @ExceptionHandler(RedirectBaseException.class)
    public CommonErrorResponse redirectBaseExceptionHandler(Exception e) {
        return new CommonErrorResponse("300", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonErrorResponse serverExceptionHandler(Exception e) {
        return new CommonErrorResponse("500", e.toString());
    }

}

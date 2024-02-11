package com.cherish.backend.exception.base;

public class ForbiddenAccessBaseException extends IllegalStateException{
    public ForbiddenAccessBaseException(String s) {
        super(s);
    }
}

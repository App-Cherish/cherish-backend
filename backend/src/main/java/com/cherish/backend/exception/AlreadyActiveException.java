package com.cherish.backend.exception;


import com.cherish.backend.exception.base.ForbiddenAccessBaseException;

public class AlreadyActiveException extends ForbiddenAccessBaseException {

    public AlreadyActiveException() {
        super("이미 활성화 된 계정입니다.");
    }
}

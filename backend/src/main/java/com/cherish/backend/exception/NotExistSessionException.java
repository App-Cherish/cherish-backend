package com.cherish.backend.exception;

import com.cherish.backend.exception.base.ForbiddenAccessBaseException;

public class NotExistSessionException extends ForbiddenAccessBaseException {

    public NotExistSessionException() {
        super("로그인을 해주세요");
    }

}

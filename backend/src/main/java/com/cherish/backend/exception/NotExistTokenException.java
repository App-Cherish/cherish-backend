package com.cherish.backend.exception;

import com.cherish.backend.exception.base.NotExistBaseException;

public class NotExistTokenException extends NotExistBaseException {

    public NotExistTokenException() {
        super("존재하지 않는 토큰입니다..");
    }
    
}

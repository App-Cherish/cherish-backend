package com.cherish.backend.exception;

import com.cherish.backend.exception.base.NotExistBaseException;

public class NotExistOauthIdException extends NotExistBaseException {

    public NotExistOauthIdException() {
        super("oauth Id의 검증이 실패하였습니다.");
    }

}

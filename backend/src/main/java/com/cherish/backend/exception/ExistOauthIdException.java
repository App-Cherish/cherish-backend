package com.cherish.backend.exception;

import com.cherish.backend.exception.base.ExistBaseException;

public class ExistOauthIdException extends ExistBaseException {

    public ExistOauthIdException() {
        super("이미 존재하는 oauthId입니다.");
    }

}

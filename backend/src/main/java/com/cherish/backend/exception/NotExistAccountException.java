package com.cherish.backend.exception;

import com.cherish.backend.exception.base.RedirectBaseException;

public class NotExistAccountException extends RedirectBaseException {

    public NotExistAccountException() {
        super("존재하지 않는 계정입니다. 회원가입을 페이지로 요청을 다시 보내주세요.");
    }
}

package com.cherish.backend.exception;

import com.cherish.backend.exception.base.NotExistBaseException;

public class NotExistBackUpException  extends NotExistBaseException {
    public NotExistBackUpException() {
        super("존재하는 않는 백업 아이디 입니다.");
    }
}

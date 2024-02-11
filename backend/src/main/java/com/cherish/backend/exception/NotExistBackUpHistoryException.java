package com.cherish.backend.exception;

import com.cherish.backend.exception.base.NotExistBaseException;

public class NotExistBackUpHistoryException extends NotExistBaseException {

    public NotExistBackUpHistoryException() {
        super("백업 기록이 존재하지 않습니다.");
    }
}

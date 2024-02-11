package com.cherish.backend.exception;


import com.cherish.backend.exception.base.ExistBaseException;

public class ExistBackUpHistory extends ExistBaseException {

    public ExistBackUpHistory() {
        super("기존에 백업 히스토리가 존재합니다.");
    }
}

package com.cherish.backend.exception;

import com.cherish.backend.exception.base.ConvertTypeBaseException;

public class EnumTypeConvertException extends ConvertTypeBaseException {

    public EnumTypeConvertException() {
        super("존재하지 않는 enum 타입입니다. 올바른 value값을 입력해주세요.");
    }
}

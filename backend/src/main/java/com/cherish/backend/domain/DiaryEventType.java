package com.cherish.backend.domain;

import com.cherish.backend.exception.EnumTypeConvertException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum DiaryEventType {

    CREATE("create"),
    MODIFIED("modified"),
    DELETE("delete");

    String value;

    DiaryEventType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Gender parsing(String inputValue) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.getValue().equals(inputValue))
                .findFirst()
                .orElseThrow(EnumTypeConvertException::new);
    }
}

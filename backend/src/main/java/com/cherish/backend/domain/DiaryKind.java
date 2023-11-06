package com.cherish.backend.domain;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum DiaryKind {

    EMOTION("감정형식"),
    QUESTION("질문형식"),
    FREE("자유형식");

    String value;

    DiaryKind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DiaryKind parsing(String inputValue) {
        return Stream.of(DiaryKind.values())
                .filter(diaryKind -> diaryKind.getValue().equals(inputValue))
                .findFirst()
                .orElse(null);
    }
}

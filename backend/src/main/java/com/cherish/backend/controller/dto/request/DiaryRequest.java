package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class DiaryRequest {

    DiaryKind kind;
    String title;
    String content;
    String date;

    public DiaryRequest(DiaryKind kind, String title, String content, String date) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.date = date;
    }
}

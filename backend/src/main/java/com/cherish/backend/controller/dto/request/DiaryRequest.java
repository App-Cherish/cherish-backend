package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.DiaryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class DiaryRequest {

    String id;
    @NotEmpty
    DiaryKind kind;
    @NotBlank
    String title;
    @NotBlank
    String content;
    @NotBlank
    String date;

    public DiaryRequest(String id, DiaryKind kind, String title, String content, String date) {
        this.id = id;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.date = date;
    }
}

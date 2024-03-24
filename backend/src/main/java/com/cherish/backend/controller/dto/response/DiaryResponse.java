package com.cherish.backend.controller.dto.response;

import com.cherish.backend.domain.DiaryKind;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryResponse {

    private final String clientId;

    private final String title;

    private final String content;

    private final String diaryKind;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime date;

    public DiaryResponse(String clientId, String title, String content, DiaryKind diaryKind, LocalDateTime clientWritingDate) {
        this.clientId = clientId;
        this.title = title;
        this.content = content;
        this.diaryKind = diaryKind.getValue();
        this.date = clientWritingDate;
    }
}

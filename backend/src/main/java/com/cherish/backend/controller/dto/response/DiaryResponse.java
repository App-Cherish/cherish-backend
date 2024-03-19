package com.cherish.backend.controller.dto.response;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryResponse {

    private final String clientId;

    private final String title;

    private final String content;

    private final String diaryKind;

    private final LocalDateTime clientWritingDate;

    public DiaryResponse(String clientId, String title, String content, DiaryKind diaryKind, LocalDateTime clientWritingDate) {
        this.clientId = clientId;
        this.title = title;
        this.content = content;
        this.diaryKind = diaryKind.getValue();
        this.clientWritingDate = clientWritingDate;
    }
}

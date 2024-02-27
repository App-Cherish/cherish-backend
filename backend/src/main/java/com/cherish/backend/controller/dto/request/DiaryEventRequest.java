package com.cherish.backend.controller.dto.request;


import com.cherish.backend.domain.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryEventRequest {


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime clientWritingDate;

    private final String title;

    private final String content;

    private final DiaryKind diaryKind;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime eventDate;

    public DiaryEventRequest(LocalDateTime clientWritingDate, String title, String content, DiaryKind diaryKind, LocalDateTime eventDate) {
        this.clientWritingDate = clientWritingDate;
        this.title = title;
        this.content = content;
        this.diaryKind = diaryKind;
        this.eventDate = eventDate;
    }

    public DiaryEvent toEventEntity(DiaryEventType diaryEventType, Avatar avatar, BackUp backUp) {
        return DiaryEvent.of(
                this.diaryKind,
                this.title,
                this.content,
                this.getClientWritingDate(),
                diaryEventType,
                this.eventDate,
                backUp,
                avatar);
    }

    public Diary toDiaryEntity(BackUp backUp, Avatar avatar) {
        return Diary.of(
                this.diaryKind,
                this.title,
                this.content,
                this.clientWritingDate,
                avatar,
                backUp);
    }
}

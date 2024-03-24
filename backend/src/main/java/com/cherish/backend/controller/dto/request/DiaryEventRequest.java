package com.cherish.backend.controller.dto.request;


import com.cherish.backend.domain.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryEventRequest {

    private final String clientId;

    private final LocalDateTime writingDate;

    private final String title;

    private final String content;

    private final DiaryKind diaryKind;

    private final LocalDateTime eventDate;

    public DiaryEventRequest(String clientId, LocalDateTime writingDate, String title, String content, DiaryKind diaryKind, LocalDateTime eventDate) {
        this.clientId = clientId;
        this.writingDate = writingDate;
        this.title = title;
        this.content = content;
        this.diaryKind = diaryKind;
        this.eventDate = eventDate;
    }

    public DiaryEvent toEventEntity(DiaryEventType diaryEventType, Avatar avatar, BackUp backUp) {
        return DiaryEvent.of(
                this.clientId,
                this.diaryKind,
                this.title,
                this.content,
                this.writingDate,
                diaryEventType,
                this.eventDate,
                backUp,
                avatar);
    }

    public Diary toDiaryEntity(BackUp backUp, Avatar avatar) {
        return Diary.of(
                this.diaryKind,
                this.clientId,
                this.title,
                this.content,
                this.writingDate,
                avatar,
                backUp);
    }
}

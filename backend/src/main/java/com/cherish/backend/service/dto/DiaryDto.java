package com.cherish.backend.service.dto;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class DiaryDto {

    String id;
    DiaryKind kind;
    String title;
    String content;
    LocalDateTime writingDate;
    String deviceId;
    String deviceType;


    public DiaryDto(DiaryKind kind, String title, String content, LocalDateTime writingDate) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.writingDate = writingDate;
    }


    public DiaryDto(String id, DiaryKind kind, String title, String content, LocalDateTime writingDate, String deviceId, String deviceType) {
        this.id = id;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.writingDate = writingDate;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}

package com.cherish.backend.service.dto;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class DiaryDto {

    String id;
    DiaryKind kind;
    String title;
    String content;
    LocalDateTime writingDate;

    public DiaryDto(String id, DiaryKind kind, String title, String content, String writingDate) {
        this.id = id;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.writingDate = stringToLocalDateTime(writingDate);
    }

    LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss +SSSS");
        return LocalDateTime.parse(date, formatter);
    }
}
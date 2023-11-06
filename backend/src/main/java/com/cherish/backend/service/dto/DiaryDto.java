package com.cherish.backend.service.dto;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class DiaryDto {

    DiaryKind kind;
    String title;
    String content;
    LocalDateTime date;

    public DiaryDto(DiaryKind kind, String title, String content, String date) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.date = stringToLocalDateTime(date);
    }

    LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss +SSSS");
        return LocalDateTime.parse(date, formatter);
    }
}

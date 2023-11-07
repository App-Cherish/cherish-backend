package com.cherish.backend.controller.dto.response;

import com.cherish.backend.domain.DiaryKind;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DiaryResponse {

    String title;
    String content;
    String kind;
    LocalDateTime writingDate;
    String deviceType;
    String deviceId;

    public DiaryResponse(String title, String content, String kind, LocalDateTime writingDate, String deviceType, String deviceId) {
        this.title = title;
        this.content = content;
        this.kind = kind;
        this.writingDate = writingDate;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }
}

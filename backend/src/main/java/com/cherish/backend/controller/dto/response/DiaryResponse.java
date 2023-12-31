package com.cherish.backend.controller.dto.response;

import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryResponse {

    String title;
    String content;
    String kind;
    String writingDate;
    String deviceType;
    String deviceId;

    public DiaryResponse(String title, String content, String kind, LocalDateTime writingDate, String deviceType, String deviceId) {
        this.title = title;
        this.content = content;
        this.kind = kind;
        this.writingDate = DateFormattingUtil.localDateTimeToString(writingDate);
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }
}

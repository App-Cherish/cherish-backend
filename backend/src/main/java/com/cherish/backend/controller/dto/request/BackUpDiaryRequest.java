package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class BackUpDiaryRequest {

    List<DiaryRequest> diary;
    String deviceType;
    String deviceId;

    public BackUpDiaryRequest(List<DiaryRequest> diary, String deviceType, String deviceId) {
        this.diary = diary;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }
}


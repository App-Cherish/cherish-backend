package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class FirstTimeBackUpDiaryRequest {

    List<DiaryRequest> diary;
    String deviceType;
    String deviceId;
    String osVersion;

    public FirstTimeBackUpDiaryRequest(List<DiaryRequest> diary, String deviceType, String deviceId, String osVersion) {
        this.diary = diary;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
    }
}


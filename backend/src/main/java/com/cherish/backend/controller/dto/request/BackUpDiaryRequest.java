package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BackUpDiaryRequest {

    List<DiaryRequest> diary;
    String deviceType;
    String deviceId;
    String osVersion;
    String backUpId;

    public BackUpDiaryRequest(List<DiaryRequest> diary, String deviceType, String deviceId, String osVersion, String backUpId) {
        this.diary = diary;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
        this.backUpId = backUpId;
    }
}


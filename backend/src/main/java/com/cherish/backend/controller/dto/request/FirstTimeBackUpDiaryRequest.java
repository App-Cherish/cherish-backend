package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FirstTimeBackUpDiaryRequest {

    List<DiaryRequest> diaryRequestList;
    String deviceType;
    String deviceId;
    String osVersion;

    public FirstTimeBackUpDiaryRequest(List<DiaryRequest> diaryRequestList, String deviceType, String deviceId, String osVersion) {
        this.diaryRequestList = diaryRequestList;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
    }
}


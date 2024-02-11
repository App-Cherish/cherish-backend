package com.cherish.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class FirstTimeBackUpDiaryRequest {

    @NotEmpty(message = "다이어리를 1개 이상 보내주세요.")
    List<DiaryRequest> diaryRequestList;
    @NotBlank
    String deviceType;
    @NotBlank
    String deviceId;
    String osVersion;

    public FirstTimeBackUpDiaryRequest(List<DiaryRequest> diaryRequestList, String deviceType, String deviceId, String osVersion) {
        this.diaryRequestList = diaryRequestList;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
    }
}


package com.cherish.backend.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BackUpDiaryRequest {

    @NotEmpty
    List<DiaryRequest> diaryRequestList;
    @NotNull
    String deviceType;
    @NotNull
    String deviceId;
    @NotNull
    String osVersion;
    @NotNull
    String backUpId;

    public BackUpDiaryRequest(List<DiaryRequest> diary, String deviceType, String deviceId, String osVersion, String backUpId) {
        this.diaryRequestList = diary;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
        this.backUpId = backUpId;
    }
}


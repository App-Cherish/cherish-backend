package com.cherish.backend.controller.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RestoreDiaryResponse {

    private final List<DiaryResponse> diaryList;

    private final String backUpId;

    private final LocalDateTime date;

    private final int count;

    private final String deviceModel;

    private final String osVersion;

    public RestoreDiaryResponse(List<DiaryResponse> diaryList, String backUpId, LocalDateTime date, int count, String deviceModel, String osVersion) {
        this.diaryList = diaryList;
        this.backUpId = backUpId;
        this.date = date;
        this.count = count;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
    }
}

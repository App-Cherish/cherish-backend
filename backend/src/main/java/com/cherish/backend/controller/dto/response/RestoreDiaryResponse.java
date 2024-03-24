package com.cherish.backend.controller.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RestoreDiaryResponse {

    private final List<DiaryResponse> recordList;

    private final BackUpHistoryResponse backupData;

    public RestoreDiaryResponse(List<DiaryResponse> diaryResponseList, BackUpHistoryResponse backUpHistoryResponse) {
        this.recordList = diaryResponseList;
        this.backupData = backUpHistoryResponse;
    }
}

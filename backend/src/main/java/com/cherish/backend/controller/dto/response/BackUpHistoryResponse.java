package com.cherish.backend.controller.dto.response;

import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BackUpHistoryResponse {

    String backUpID;
    String osVersion;
    String deviceType;
    int diaryCount;
    String createdDate;

    public BackUpHistoryResponse(String backUpID, String osVersion, String deviceType, int diaryCount, LocalDateTime createdDate) {
        this.backUpID = backUpID;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.diaryCount = diaryCount;
        this.createdDate = DateFormattingUtil.localDateTimeToString(createdDate);
    }
}

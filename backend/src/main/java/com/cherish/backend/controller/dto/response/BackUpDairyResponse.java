package com.cherish.backend.controller.dto.response;

import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BackUpDairyResponse {

    String osVersion;
    String deviceType;
    String backUpId;
    String saveTime;
    int count;

    public BackUpDairyResponse(String osVersion, String deviceType, String backUpId, LocalDateTime saveTime, int count) {
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.backUpId = backUpId;
        this.saveTime = DateFormattingUtil.localDateTimeToString(saveTime);
        this.count = count;
    }
}

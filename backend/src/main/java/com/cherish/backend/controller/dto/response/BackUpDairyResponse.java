package com.cherish.backend.controller.dto.response;

import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BackUpDairyResponse {

    String osVersion;
    String deviceType;
    String backUpId;
    String saveTime;

    public BackUpDairyResponse(String osVersion, String deviceType, String backUpId, LocalDateTime saveTime) {
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.backUpId = backUpId;
        this.saveTime = DateFormattingUtil.localDateTimeToString(saveTime);
    }
}

package com.cherish.backend.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BackUpDairyResponse {

    String osVersion;
    String deviceType;
    String backUpId;
    LocalDateTime saveTime;
    int count;

    public BackUpDairyResponse(String osVersion, String deviceType, String backUpId, LocalDateTime saveTime, int count) {
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.backUpId = backUpId;
        this.saveTime = saveTime;
        this.count = count;
    }
}

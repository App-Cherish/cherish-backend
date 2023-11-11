package com.cherish.backend.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DiarySaveResponseDto {

    String osVersion;
    String deviceType;
    String backUpId;
    int count;
    LocalDateTime saveTime;

    public DiarySaveResponseDto(String osVersion, String deviceType, String backUpId, int count, LocalDateTime saveTime) {
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.backUpId = backUpId;
        this.count = count;
        this.saveTime = saveTime;
    }
}

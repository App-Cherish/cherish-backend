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
    LocalDateTime saveTime;

    public DiarySaveResponseDto(String osVersion, String deviceType, String backUpId, LocalDateTime saveTime) {
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.backUpId = backUpId;
        this.saveTime = saveTime;
    }
}

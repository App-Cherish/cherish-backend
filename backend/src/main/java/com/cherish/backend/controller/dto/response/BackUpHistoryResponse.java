package com.cherish.backend.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BackUpHistoryResponse {


    private final String backUpId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime date;

    private final String deviceModel;

    private final String osVersion;

    private final Integer count;

    public BackUpHistoryResponse(String backUpId, LocalDateTime date, String deviceModel, String osVersion, Integer count) {
        this.backUpId = backUpId;
        this.date = date;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.count = count;
    }
}

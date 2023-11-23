package com.cherish.backend.service.dto;

import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;

import java.util.List;

@Getter
public class FirstTimeBackUpDto {

    List<DiaryDto> diaryDtos;
    String deviceType;
    String deviceId;
    String osVersion;

    public FirstTimeBackUpDto(List<DiaryDto> diaryDtos, String deviceType, String deviceId, String osVersion) {
        this.diaryDtos = diaryDtos;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
    }

    public static FirstTimeBackUpDto of(FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest) {
        List<DiaryDto> list = firstTimeBackUpDiaryRequest.getDiary().stream().map(element -> (new DiaryDto(
                element.getId(), element.getKind(), element.getTitle(), element.getContent(), DateFormattingUtil.stringDateFormatToLocalDateTime(element.getDate()), firstTimeBackUpDiaryRequest.getDeviceId(), firstTimeBackUpDiaryRequest.getDeviceType()
        ))).toList();

        return new FirstTimeBackUpDto(list, firstTimeBackUpDiaryRequest.getDeviceType(), firstTimeBackUpDiaryRequest.getDeviceId(), firstTimeBackUpDiaryRequest.getOsVersion());
    }


}

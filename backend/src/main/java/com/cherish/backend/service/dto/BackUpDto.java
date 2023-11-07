package com.cherish.backend.service.dto;

import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BackUpDto {

    List<DiaryDto> diaryDtos;
    String deviceType;
    String deviceId;
    String osVersion;
    String backUpId;

    public BackUpDto(List<DiaryDto> diaryDtos, String deviceType, String deviceId, String osVersion, String backUpId) {
        this.diaryDtos = diaryDtos;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.osVersion = osVersion;
        this.backUpId = backUpId;
    }

    public static BackUpDto of(BackUpDiaryRequest backUpDiaryRequest){
        List<DiaryDto> list = backUpDiaryRequest.getDiary().stream().map(element -> (new DiaryDto(
                element.getId(),element.getKind(), element.getTitle(), element.getContent(), element.getDate()
        ))).toList();

        return new BackUpDto(list, backUpDiaryRequest.getDeviceType(), backUpDiaryRequest.getDeviceId(), backUpDiaryRequest.getOsVersion(),backUpDiaryRequest.getBackUpId());
    }


}

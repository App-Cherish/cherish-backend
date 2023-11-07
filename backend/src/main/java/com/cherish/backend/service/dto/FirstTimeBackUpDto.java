package com.cherish.backend.service.dto;

import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
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

    public static FirstTimeBackUpDto of(FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest){
        List<DiaryDto> list = firstTimeBackUpDiaryRequest.getDiary().stream().map(element -> (new DiaryDto(
                element.getId(),element.getKind(), element.getTitle(), element.getContent(), element.getDate()
        ))).toList();

        return new FirstTimeBackUpDto(list, firstTimeBackUpDiaryRequest.getDeviceType(), firstTimeBackUpDiaryRequest.getDeviceId(), firstTimeBackUpDiaryRequest.getOsVersion());
    }


}

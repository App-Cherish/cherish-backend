package com.cherish.backend.service.dto;

import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BackUpDiaryDto {

    List<DiaryDto> diaryDtos;
    String deviceType;
    String deviceId;

    public BackUpDiaryDto(List<DiaryDto> diaryDtos, String deviceType, String deviceId) {
        this.diaryDtos = diaryDtos;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public static BackUpDiaryDto of(BackUpDiaryRequest backUpDiaryRequest){
        List<DiaryDto> list = backUpDiaryRequest.getDiary().stream().map(element -> (new DiaryDto(
                element.getKind(), element.getTitle(), element.getContent(), element.getDate()
        ))).toList();

        return new BackUpDiaryDto(list, backUpDiaryRequest.getDeviceType(), backUpDiaryRequest.getDeviceId());
    }


}

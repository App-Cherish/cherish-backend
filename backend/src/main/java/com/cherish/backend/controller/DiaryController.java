package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.service.DiaryService;
import com.cherish.backend.service.dto.BackUpDto;
import com.cherish.backend.service.dto.DiarySaveResponseDto;
import com.cherish.backend.service.dto.FirstTimeBackUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/firsttimebackup")
    public BackUpDairyResponse firstTimeBackUp(@RequestBody FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest, @LoginAvatarId Long avatarId) {
        DiarySaveResponseDto diarySaveResponseDto = diaryService.firstTimeBackUp(FirstTimeBackUpDto.of(firstTimeBackUpDiaryRequest), avatarId);
        return new BackUpDairyResponse(diarySaveResponseDto.getOsVersion(), diarySaveResponseDto.getDeviceType(), diarySaveResponseDto.getBackUpId(), diarySaveResponseDto.getSaveTime());
    }

    @PostMapping("/backup")
    public BackUpDairyResponse backUp(@RequestBody BackUpDiaryRequest backUpDiaryRequest, @LoginAvatarId Long avatarId){
        DiarySaveResponseDto diarySaveResponseDto = diaryService.backUp(BackUpDto.of(backUpDiaryRequest),avatarId);
        return new BackUpDairyResponse(diarySaveResponseDto.getOsVersion(), diarySaveResponseDto.getDeviceType(), diarySaveResponseDto.getBackUpId(), diarySaveResponseDto.getSaveTime());
    }


}

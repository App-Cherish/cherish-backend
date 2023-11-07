package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.service.DiaryService;
import com.cherish.backend.service.dto.BackUpDto;
import com.cherish.backend.service.dto.DiaryDto;
import com.cherish.backend.service.dto.DiarySaveResponseDto;
import com.cherish.backend.service.dto.FirstTimeBackUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping
    public List<DiaryResponse> getDiaryListByBackUpId(@RequestParam(name = "id") String backUpId, @LoginAvatarId Long avatarId) {
        List<DiaryDto> diaryList = diaryService.getRecentDiaryList(backUpId, avatarId);

        List<DiaryResponse> list = diaryList.stream().map(d ->
                new DiaryResponse(d.getTitle(),
                        d.getContent(),
                        d.getKind().getValue(),
                        d.getWritingDate(),
                        d.getDeviceType(),
                        d.getDeviceId())).toList();

        return list;
    }
}


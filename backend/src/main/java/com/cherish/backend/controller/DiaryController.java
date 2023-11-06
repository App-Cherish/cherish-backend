package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.service.DiaryService;
import com.cherish.backend.service.dto.BackUpDiaryDto;
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

    @PostMapping("/backup")
    public BackUpDairyResponse backup(@RequestBody BackUpDiaryRequest backUpDiaryRequest, @LoginAvatarId Long avatarId) {

        diaryService.backUp(BackUpDiaryDto.of(backUpDiaryRequest), avatarId);

        return new BackUpDairyResponse();
    }


}

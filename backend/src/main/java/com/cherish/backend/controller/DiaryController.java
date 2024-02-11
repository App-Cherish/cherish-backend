package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.service.DiaryService;
import jakarta.validation.Valid;
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
    public BackUpDairyResponse firstTimeBackUp(@RequestBody @Valid FirstTimeBackUpDiaryRequest firstTimeBackUpDiaryRequest, @LoginAvatarId Long avatarId) {
        return diaryService.firstTimeBackUp(firstTimeBackUpDiaryRequest, avatarId);
    }

    @PostMapping("/backup")
    public BackUpDairyResponse backUp(@RequestBody @Valid BackUpDiaryRequest backUpDiaryRequest, @LoginAvatarId Long avatarId) {
        return diaryService.backUp(backUpDiaryRequest, avatarId);
    }


    @GetMapping
    public List<DiaryResponse> getDiaryListByBackUpId(@RequestParam(name = "id") String backUpId, @LoginAvatarId Long avatarId) {
        return diaryService.getRecentDiaryList(backUpId, avatarId);
    }
}


package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.controller.dto.response.RestoreDiaryResponse;
import com.cherish.backend.service.BackUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
public class BackUpController {

    private final BackUpService backUpService;

    @GetMapping
    public BackUpHistoryResponse getRecentBackUp(@LoginAvatarId Long avatarId) {
        return backUpService.getBackUp(avatarId);
    }

    @PostMapping
    public BackUpHistoryResponse backUpDiary(@RequestBody DiaryEventRequestList diaryEventRequest, @LoginAvatarId Long avatarId) {
        return backUpService.backup(diaryEventRequest, avatarId);
    }

    @GetMapping("/restore")
    public RestoreDiaryResponse restore(@RequestParam(name = "backupId") String backupId, @LoginAvatarId Long avatarId) {
        return backUpService.restoreDiary(avatarId, backupId);
    }


}

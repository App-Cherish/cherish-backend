package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.service.BackUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
public class BackUpController {

    private final BackUpService backUpService;

    @PostMapping
    public void backUpDiary(@RequestBody DiaryEventRequestList diaryEventRequest, @LoginAvatarId Long avatarId) {
        backUpService.backup(diaryEventRequest, avatarId);
    }


}

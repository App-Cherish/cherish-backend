package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.service.BackUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackUpController {

    private final BackUpService backUpService;

    @GetMapping
    public BackUpHistoryResponse loadRecentBackUpHistory(@LoginAvatarId Long avatarId) {
        return backUpService.getRecentBackUp(avatarId);
    }
}

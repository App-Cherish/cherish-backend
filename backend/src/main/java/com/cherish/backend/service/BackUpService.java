package com.cherish.backend.service;

import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.exception.NotExistBackUpHistoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackUpService {

    private final BackUpRepository backUpRepository;

    public BackUpHistoryResponse getRecentBackUp(Long avatarId) {
        BackUp findBackUp = backUpRepository.findBackUpByIdLatest(avatarId).orElseThrow(NotExistBackUpHistoryException::new);

        return new BackUpHistoryResponse(
                findBackUp.getId(),
                findBackUp.getOsVersion(),
                findBackUp.getDeviceType(),
                findBackUp.getDiaryCount(),
                findBackUp.getCreatedDate());
    }

}

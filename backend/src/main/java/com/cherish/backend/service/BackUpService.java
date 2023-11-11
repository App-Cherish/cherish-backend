package com.cherish.backend.service;

import com.cherish.backend.domain.BackUp;
import com.cherish.backend.repositroy.BackUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackUpService {

    private final BackUpRepository backUpRepository;

    public BackUp getRecentBackUp(Long avatarId) {
        Optional<BackUp> findBackUp = backUpRepository.findBackUpByIdLatest(avatarId);
        if (findBackUp.isEmpty()) {
            throw new IllegalArgumentException("백업 기록이 존재하지 않습니다.");
        }

        return findBackUp.get();
    }

}

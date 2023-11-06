package com.cherish.backend.service;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Diary;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.dto.BackUpDiaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final AvatarRepository avatarRepository;

    @Transactional
    public void backUp(BackUpDiaryDto backUpDiaryDto, Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).get();
        List<Diary> list = backUpDiaryDto.getDiaryDtos().stream().map(element ->
                Diary.of(
                        element.getKind(), element.getTitle(), element.getContent(), element.getDate(), backUpDiaryDto.getDeviceType(), backUpDiaryDto.getDeviceId(), findAvatar
                )
        ).toList();
        diaryRepository.saveAll(list);
    }
}

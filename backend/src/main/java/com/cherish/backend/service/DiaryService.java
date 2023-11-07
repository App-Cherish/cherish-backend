package com.cherish.backend.service;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Diary;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.service.dto.BackUpDto;
import com.cherish.backend.service.dto.DiarySaveResponseDto;
import com.cherish.backend.service.dto.FirstTimeBackUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final AvatarRepository avatarRepository;
    private final BackUpRepository backUpRepository;

    @Transactional
    public DiarySaveResponseDto firstTimeBackUp(FirstTimeBackUpDto firstTimeBackUpDto, Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).get();
        String id = createId();
        BackUp backUp = saveBackUP(BackUp.of(id, firstTimeBackUpDto.getOsVersion(), firstTimeBackUpDto.getDeviceType(), firstTimeBackUpDto.getDiaryDtos().size()));
        List<Diary> createDiaryList = createDiaryList(firstTimeBackUpDto, findAvatar, backUp);
        diaryRepository.saveAll(createDiaryList);

        return new DiarySaveResponseDto(firstTimeBackUpDto.getOsVersion(), firstTimeBackUpDto.getDeviceType(), backUp.getId(), backUp.getCreatedDate());
    }

    @Transactional
    public DiarySaveResponseDto backUp(BackUpDto backUpDto, Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).get();
        String id = createId();
        BackUp backUp = saveBackUP(BackUp.of(id, backUpDto.getOsVersion(), backUpDto.getDeviceType(), backUpDto.getDiaryDtos().size()));

        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(backUpDto.getBackUpId(), avatarId);

        for (Diary diary : findDiaryList) {
            diary.modifiedBackUp(backUp);
        }


        diaryRepository.saveAll(createNewDiaryList(backUpDto, findAvatar, backUp));
        return new DiarySaveResponseDto(backUpDto.getOsVersion(), backUpDto.getDeviceType(), backUp.getId(), backUp.getCreatedDate());
    }


    @Transactional
    public BackUp saveBackUP(BackUp backUp) {
        return backUpRepository.save(backUp);
    }

    private List<Diary> createDiaryList(FirstTimeBackUpDto firstTimeBackUpDto, Avatar avatar, BackUp backUp) {
        return firstTimeBackUpDto.getDiaryDtos().stream().map(element ->
                Diary.of(
                        element.getKind(), element.getTitle(), element.getContent(), element.getWritingDate(), firstTimeBackUpDto.getDeviceType(), firstTimeBackUpDto.getDeviceId(), avatar, backUp
                )
        ).toList();
    }

    private List<Diary> createNewDiaryList(BackUpDto backUpDto, Avatar avatar, BackUp backUp) {
        return backUpDto.getDiaryDtos().stream().filter(d -> d.getId() == null).map(element ->
                Diary.of(
                        element.getKind(), element.getTitle(), element.getContent(), element.getWritingDate(), backUpDto.getDeviceType(), backUpDto.getDeviceId(), avatar, backUp
                )
        ).toList();
    }

    private String createId() {
        return UUID.randomUUID().toString().split("-")[0];
    }


}

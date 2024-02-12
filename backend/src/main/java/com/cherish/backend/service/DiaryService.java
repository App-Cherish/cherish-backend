package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.BackUpDiaryRequest;
import com.cherish.backend.controller.dto.request.DiaryRequest;
import com.cherish.backend.controller.dto.request.FirstTimeBackUpDiaryRequest;
import com.cherish.backend.controller.dto.response.BackUpDairyResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Diary;
import com.cherish.backend.exception.ExistBackUpHistory;
import com.cherish.backend.exception.NotExistAvatarException;
import com.cherish.backend.exception.NotExistBackUpException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import com.cherish.backend.util.DateFormattingUtil;
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
    private final BackUpRepository backUpRepository;

    @Transactional
    public BackUpDairyResponse firstTimeBackUp(FirstTimeBackUpDiaryRequest backUpDiaryRequest, Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).orElseThrow(NotExistAvatarException::new);

        if (backUpRepository.ExistBackUpByAvatarId(avatarId)) {
            throw new ExistBackUpHistory();
        }

        BackUp backUp = backUpRepository.save(BackUp.of(backUpDiaryRequest.getOsVersion(), backUpDiaryRequest.getDeviceType(), findAvatar));
        List<Diary> createDiaryList = createDiaryList(backUpDiaryRequest, findAvatar, backUp);
        diaryRepository.saveAllAndFlush(createDiaryList);

        return new BackUpDairyResponse(backUpDiaryRequest.getOsVersion(), backUpDiaryRequest.getDeviceType(), backUp.getId(), backUp.getCreatedDate());
    }

    @Transactional
    public BackUpDairyResponse backUp(BackUpDiaryRequest backUpDiaryRequest, Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).orElseThrow(NotExistAvatarException::new);
        BackUp findBackUp = backUpRepository.findById(backUpDiaryRequest.getBackUpId()).orElseThrow(NotExistBackUpException::new);
        BackUp backUp = backUpRepository.save(findBackUp.renew(backUpDiaryRequest.getOsVersion(), backUpDiaryRequest.getDeviceType()));

        findBackUp.deActive();

        for (DiaryRequest diaryRequest : backUpDiaryRequest.getDiaryRequestList()) {
            if (diaryRequest.getId() == null) {
                Diary diaryByIdAndAvatarIdAndBackUpId = diaryRepository.findDiaryByIdAndAvatarId(diaryRequest.getId(), avatarId);
                diaryByIdAndAvatarIdAndBackUpId.modifiedBackUp(backUp);
            }
        }

        diaryRepository.saveAllAndFlush(renewDiaryList(backUpDiaryRequest, findAvatar, backUp));

        return new BackUpDairyResponse(backUpDiaryRequest.getOsVersion(), backUpDiaryRequest.getDeviceType(), backUp.getId(), backUp.getCreatedDate());
    }

    public List<DiaryResponse> getRecentDiaryList(String backUpId, Long avatarId) {
        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(backUpId, avatarId);

        if (findDiaryList.isEmpty()) {
            throw new NotExistBackUpException();
        }

        return findDiaryList.stream()
                .map(d -> new DiaryResponse(
                        d.getTitle(),
                        d.getContent(),
                        d.getKind().getValue(),
                        d.getWritingDate(),
                        d.getDeviceType(),
                        d.getDeviceId())
                ).toList();
    }

    private List<Diary> createDiaryList(FirstTimeBackUpDiaryRequest backUpDiaryRequest, Avatar avatar, BackUp backUp) {

        return backUpDiaryRequest.getDiaryRequestList().stream().map(element ->
                Diary.of(
                        element.getKind(),
                        element.getTitle(),
                        element.getContent(),
                        DateFormattingUtil.stringDateFormatToLocalDateTime(element.getDate()),
                        backUpDiaryRequest.getDeviceType(),
                        backUpDiaryRequest.getDeviceId(),
                        avatar,
                        backUp)).toList();
    }


    private List<Diary> renewDiaryList(BackUpDiaryRequest backUpDiaryRequest, Avatar avatar, BackUp backUp) {
        return backUpDiaryRequest.getDiaryRequestList().stream().filter(d -> d.getId() == null).map(element ->
                Diary.of(
                        element.getKind(),
                        element.getTitle(),
                        element.getContent(),
                        DateFormattingUtil.stringDateFormatToLocalDateTime(element.getDate()),
                        backUpDiaryRequest.getDeviceType(),
                        backUpDiaryRequest.getDeviceId(),
                        avatar,
                        backUp
                )
        ).toList();
    }



}

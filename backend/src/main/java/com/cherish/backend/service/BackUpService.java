package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.DiaryEventRequest;
import com.cherish.backend.controller.dto.request.DiaryEventRequestList;
import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.controller.dto.response.DiaryResponse;
import com.cherish.backend.controller.dto.response.RestoreDiaryResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Diary;
import com.cherish.backend.domain.DiaryEvent;
import com.cherish.backend.exception.NotExistAvatarException;
import com.cherish.backend.exception.NotExistBackUpHistoryException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.DiaryEventRepository;
import com.cherish.backend.repositroy.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackUpService {

    private final BackUpRepository backUpRepository;
    private final AvatarRepository avatarRepository;
    private final DiaryEventRepository diaryEventRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public BackUpHistoryResponse backup(DiaryEventRequestList requestList, Long avatarId) {
        Optional<BackUp> findBackUp = backUpRepository.findBackUpByIdLatest(avatarId);
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).orElseThrow(NotExistAvatarException::new);
        BackUp backUp = BackUp.of(requestList.getOsVersion(), requestList.getDeviceType(), findAvatar);

        List<DiaryEvent> createDiaryEventList = requestList.toCreateDiaryEventEntity(findAvatar, backUp);

        Map<String, DiaryEventRequest> editLastEventMap = requestList.toEditEventMapByLastEventDate();
        List<Diary> diaryList = requestList.toCreateDiaryEntityModifiedByEditEventList(editLastEventMap, findAvatar, backUp);

        diaryList = requestList.createDiaryListDeleteByDeleteEventList(diaryList);

        Integer count = diaryList.size();

        diaryRepository.saveAll(diaryList);

        if (findBackUp.isPresent()) {
            List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(findBackUp.get().getId(), avatarId);

            findDiaryList.forEach(d -> d.updateBackUp(backUp));

            List<Diary> editDiaryList = requestList.toEditDiaryListFromFindDiaryList(findDiaryList, findAvatar);

            List<Diary> deleteDiaryList = requestList.toDeleteDiaryListFromFindDiaryList(findDiaryList, findAvatar);

            count += editDiaryList.size();

            backUpRepository.deActiveBackUp(findBackUp.get().getId());
            diaryRepository.updateDiaryList(editDiaryList);
            diaryRepository.deActiveDiaryList(deleteDiaryList);
        }
        backUp.setCount(count);
        backUpRepository.save(backUp);
        diaryEventRepository.saveAll(createDiaryEventList);

        List<DiaryEvent> diaryEditEvents = requestList.toEditDiaryEventEntity(findAvatar, backUp);
        List<DiaryEvent> diaryDeleteEvents = requestList.toDeleteDiaryEventEntity(findAvatar, backUp);
        diaryEventRepository.saveAll(diaryEditEvents);
        diaryEventRepository.saveAll(diaryDeleteEvents);

        return new BackUpHistoryResponse(backUp.getId(), backUp.getCreatedDate(), backUp.getDeviceType(), backUp.getOsVersion(), backUp.getCount());
    }

    public BackUpHistoryResponse getBackUp(Long avatarId) {
        return backUpRepository.findBackUpByIdLatest(avatarId).orElseThrow(NotExistBackUpHistoryException::new).toDto();
    }

    public RestoreDiaryResponse restoreDiary(Long avatarId, String backUpId) {
        List<Diary> diaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(backUpId, avatarId);

        if (diaryList.isEmpty()) {
            throw new NotExistBackUpHistoryException();
        }

        List<DiaryResponse> diaryResponse = diaryList.stream()
                .map(d -> new DiaryResponse(d.getClientId(), d.getTitle(), d.getContent(), d.getKind(), d.getClientWritingDate()))
                .toList();

        BackUp backUp = diaryList.get(0).getBackUp();

        BackUpHistoryResponse backUpHistoryResponse = new BackUpHistoryResponse(backUp.getId(), backUp.getCreatedDate(), backUp.getDeviceType(), backUp.getOsVersion(), backUp.getCount());

        return new RestoreDiaryResponse(diaryResponse, backUpHistoryResponse);
    }

}

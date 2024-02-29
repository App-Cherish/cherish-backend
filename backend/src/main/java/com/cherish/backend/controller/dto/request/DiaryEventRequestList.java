package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DiaryEventRequestList {

    private final List<DiaryEventRequest> createEventList;

    private final List<DiaryEventRequest> editEventList;

    private final List<DiaryEventRequest> deleteEventList;

    private final String osVersion;
    private final String deviceType;


    public DiaryEventRequestList(List<DiaryEventRequest> createEventList, List<DiaryEventRequest> editEventList, List<DiaryEventRequest> deleteEventList, String osVersion, String deviceType) {
        this.createEventList = createEventList;
        this.editEventList = editEventList;
        this.deleteEventList = deleteEventList;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
    }

    public List<DiaryEvent> toCreateDiaryEventEntity(Avatar avatar, BackUp backUp) {
        if (this.createEventList == null) {
            return new ArrayList<>();
        }

        return this.createEventList.stream()
                .map(e -> e.toEventEntity(DiaryEventType.CREATE, avatar, backUp))
                .toList();
    }

    public List<DiaryEvent> toEditDiaryEventEntity(Avatar avatar, BackUp backUp) {
        if (this.editEventList == null) {
            return new ArrayList<>();
        }

        return this.editEventList.stream()
                .map(e -> e.toEventEntity(DiaryEventType.MODIFIED, avatar, backUp))
                .toList();
    }

    public List<DiaryEvent> toDeleteDiaryEventEntity(Avatar avatar, BackUp backUp) {
        if (this.deleteEventList == null) {
            return new ArrayList<>();
        }

        return this.deleteEventList.stream()
                .map(e -> e.toEventEntity(DiaryEventType.DELETE, avatar, backUp))
                .toList();
    }

    public Map<LocalDateTime, DiaryEventRequest> toEditEventMapByLastEventDate() {
        Map<LocalDateTime, DiaryEventRequest> editEventMap = new HashMap<>();

        if (this.editEventList == null) {
            return editEventMap;
        }

        for (DiaryEventRequest diaryEventRequest : this.getEditEventList()) {
            if (editEventMap.containsKey(diaryEventRequest.getClientWritingDate())) {
                if (editEventMap.get(diaryEventRequest.getClientWritingDate()).getEventDate().isBefore(diaryEventRequest.getEventDate())) {
                    editEventMap.replace(diaryEventRequest.getClientWritingDate(), diaryEventRequest);
                }
            } else {
                editEventMap.put(diaryEventRequest.getClientWritingDate(), diaryEventRequest);
            }
        }

        return editEventMap;
    }

    public List<Diary> toEditDiaryListFromFindDiaryList(List<Diary> findDiaryList, Avatar avatar) {
        if (this.editEventList == null) {
            return new ArrayList<>();
        }

        return findDiaryList.stream()
                .filter(diary -> toEditEventMapByLastEventDate().containsKey(diary.getClientWritingDate()))
                .map(diary -> {
                    DiaryEventRequest request = toEditEventMapByLastEventDate().get(diary.getClientWritingDate());
                    return Diary.of(diary.getId(), diary.getClientId(), request.getDiaryKind(), request.getTitle(), request.getContent(), request.getClientWritingDate(), avatar, diary.getBackUp());
                })
                .toList();
    }

    public List<Diary> toDeleteDiaryListFromFindDiaryList(List<Diary> findDiaryList, Avatar avatar) {
        if (this.deleteEventList == null) {
            return new ArrayList<>();
        }

        BackUp backUp = findDiaryList.get(0).getBackUp();

        return findDiaryList.stream()
                .filter(diary -> this.getDeleteEventList().stream()
                        .anyMatch(request -> request.getClientWritingDate().equals(diary.getClientWritingDate())))
                .map(e -> Diary.of(e.getId(), e.getClientId(), e.getKind(), e.getTitle(), e.getContent(), e.getClientWritingDate(), avatar, backUp))
                .toList();
    }


    public List<Diary> toCreateDiaryEntityModifiedByEditEventList(Map<LocalDateTime, DiaryEventRequest> editLastEventMap, Avatar avatar, BackUp backUp) {
        if (this.createEventList == null) {
            return new ArrayList<>();
        }

        if (editLastEventMap.isEmpty()) {
            return this.createEventList.stream()
                    .map(e -> Diary.of(e.getDiaryKind(), e.getClientId(), e.getTitle(), e.getContent(), e.getClientWritingDate(), avatar, backUp))
                    .toList();
        }

        return toCreateDiaryEventEntity(avatar, backUp).stream()
                .map(diaryEvent -> editLastEventMap.containsKey(diaryEvent.getClientWritingDate())
                        ? editLastEventMap.get(diaryEvent.getClientWritingDate()).toEventEntity(DiaryEventType.CREATE, avatar, backUp)
                        : diaryEvent)
                .map(event -> Diary.of(event.getKind(), event.getClientId(), event.getTitle(), event.getContent(), event.getClientWritingDate(), event.getAvatar(), event.getBackUp())).toList();
    }


    public List<Diary> createDiaryListDeleteByDeleteEventList(List<Diary> diaryList) {
        if (this.deleteEventList == null) {
            return diaryList;
        }

        return diaryList.stream()
                .filter(d -> this.deleteEventList.stream()
                        .noneMatch(e -> e.getClientWritingDate().isEqual(d.getClientWritingDate())))
                .map(d -> Diary.of(d.getKind(), d.getClientId(), d.getTitle(), d.getContent(), d.getClientWritingDate(), d.getAvatar(), d.getBackUp()))
                .toList();
    }
}

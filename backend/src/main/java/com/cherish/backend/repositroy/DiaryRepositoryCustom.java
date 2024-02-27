package com.cherish.backend.repositroy;

import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Diary;

import java.util.List;

public interface DiaryRepositoryCustom {

    List<Diary> findDiariesByIdAndAvatarIdAndBackUpId(String backUpId, Long avatarId);

    void updateDiary(Diary newDiary);

    void updateDiaryList(List<Diary> editDirayList);

    void deActiveDiary(Diary newDiary);

    void deActiveDiaryList(List<Diary> deleteDiaryList);

    void updateBackUp(BackUp updateBackUp, Long diaryId);
}

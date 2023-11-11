package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Diary;

import java.util.List;

public interface DiaryRepositoryCustom {

    Diary findDiaryByIdAndAvatarId(String diaryId, Long avatarId);

    List<Diary> findDiariesByIdAndAvatarIdAndBackUpId(String backUpId ,Long avatarId);
}

package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Diary;

import java.util.List;

public interface DiaryRepositoryCustom {

    List<Diary> findDiariesByIdAndAvatarIdAndBackUpId(String id, Long avatarId);

}

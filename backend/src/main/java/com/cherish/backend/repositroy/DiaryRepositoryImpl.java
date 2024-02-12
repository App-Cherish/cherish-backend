package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Diary;
import com.cherish.backend.domain.QDiary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Diary findDiaryByIdAndAvatarId(Long diaryId, Long avatarId) {
        QDiary diary = new QDiary("di");

        return queryFactory.selectFrom(diary)
                .from(diary)
                .where((diary.avatar.id.eq(avatarId)).and(diary.id.eq(diaryId)))
                .fetchOne();
    }

    @Override
    public List<Diary> findDiariesByIdAndAvatarIdAndBackUpId(String backUpId, Long avatarId) {
        QDiary diary = new QDiary("di");

        return queryFactory.selectFrom(diary)
                .from(diary)
                .where(diary.backUp.id.eq(backUpId).and((diary.avatar.id.eq(avatarId))))
                .fetchAll().fetch();
    }
}

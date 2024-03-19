package com.cherish.backend.repositroy;

import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Diary;
import com.cherish.backend.domain.QDiary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Diary> findDiariesByIdAndAvatarIdAndBackUpId(String backUpId, Long avatarId) {
        QDiary diary = QDiary.diary;
        return queryFactory.selectFrom(diary)
                .from(diary)
                .where(diary.backUp.id.eq(backUpId)
                        .and((diary.avatar.id.eq(avatarId)))
                        .and(diary.active.eq(1)))
                .fetchAll().fetch();
    }

    @Override
    public void updateDiary(Diary newDiary) {
        QDiary diary = QDiary.diary;

        queryFactory.update(diary)
                .set(diary.title, newDiary.getTitle())
                .set(diary.content, newDiary.getContent())
                .set(diary.kind, newDiary.getKind())
                .where(diary.id.eq(newDiary.getId()))
                .execute();


    }

    public void deActiveDiary(Diary newDiary) {
        QDiary diary = QDiary.diary;

        queryFactory.update(diary)
                .set(diary.active, 0)
                .where(diary.id.eq(newDiary.getId()))
                .execute();
    }


    @Override
    public void updateDiaryList(List<Diary> editDirayList) {
        editDirayList.forEach(this::updateDiary);
    }

    @Override
    public void deActiveDiaryList(List<Diary> deleteDiaryList) {
        deleteDiaryList.forEach(this::deActiveDiary);
    }

    @Override
    public void updateBackUp(BackUp updateBackUp, Long diaryId) {
        QDiary diary = QDiary.diary;
        queryFactory.update(diary)
                .set(diary.backUp.id, updateBackUp.getId())
                .where(diary.id.eq(diaryId))
                .execute();
    }
}

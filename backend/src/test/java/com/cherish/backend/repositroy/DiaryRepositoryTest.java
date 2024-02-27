package com.cherish.backend.repositroy;

import com.cherish.backend.domain.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class DiaryRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    BackUpRepository backUpRepository;

    Avatar avatar;

    BackUp backUp;

    Diary diary1, diary2, diary3;


    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        backUp = BackUp.of("os1", "device1", avatar);

        em.persist(avatar);
        em.persist(backUp);

        diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), avatar, backUp);
        diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), avatar, backUp);
        diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), avatar, backUp);


        em.persist(diary1);
        em.persist(diary2);
        em.persist(diary3);
    }

    @Test
    @DisplayName("여려개의 Diary entity가 데이터베이스에 저장되어있을 때 아바타 엔티티와 아이디와 백업 엔티티의 아이디로 조회가 가능해야 한다.")
    public void findDiariesByIdAndAvatarIdAndBackUpIdTest() throws Exception {
        //given

        //when

        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(backUp.getId(), avatar.getId());
        //then

        assertThat(findDiaryList.size()).isEqualTo(3);
        assertThat(findDiaryList).extracting("id", Long.class).contains(diary1.getId(), diary2.getId(), diary3.getId());
    }

    @Test
    @DisplayName("업데이트 시에 요쳥된 값에 대해서 변경이 이루어져야 하며 백업 값이 갱신 되어야 한다.")
    public void updateTest() throws Exception {
        BackUp newBackUp = BackUp.of(backUp.getOsVersion(), backUp.getDeviceType(), avatar);
        em.persist(newBackUp);
        em.flush();
        //when
        Diary findDiary = em.find(Diary.class, diary1.getId());
        Diary updateDiary = Diary.of(findDiary.getId(), DiaryKind.QUESTION, "editTitle", "editContent", diary1.getClientWritingDate(), diary1.getAvatar(), newBackUp);
        diaryRepository.updateDiary(updateDiary);
        //then
        em.flush();
        em.clear();
        Diary result = em.find(Diary.class, updateDiary.getId());
        assertThat(result.getId()).isEqualTo(updateDiary.getId());
        assertThat(result.getTitle()).isEqualTo(updateDiary.getTitle());
        assertThat(result.getContent()).isEqualTo(updateDiary.getContent());
    }

    @Test
    @DisplayName("한개의 일기에 대해서 비활성화 요청이 들어온 경우 해당 일기값 들이 비활성화 되어야 한다.")
    public void deActiveTest() throws Exception {
        //given
        //when
        diaryRepository.deActiveDiary(diary1);
        //then
        em.flush();
        em.clear();
        Diary expect = em.find(Diary.class, diary1.getId());
        assertThat(expect.getActive()).isEqualTo(0);
    }

    @Test
    @DisplayName("여러개의 일기에 대해서 비활성화 요청이 들어온 경우 해당 일기값 들이 비활성화 되어야 한다.")
    public void deActiveListTest() throws Exception {
        //given
        //when
        List<Diary> list = new ArrayList<>();
        list.add(diary1);
        list.add(diary2);
        list.add(diary3);

        diaryRepository.deActiveDiaryList(list);
        //then
        em.flush();
        em.clear();
        Diary findDiary1 = em.find(Diary.class, diary1.getId());
        Diary findDiary2 = em.find(Diary.class, diary2.getId());
        Diary findDiary3 = em.find(Diary.class, diary3.getId());

        assertThat(findDiary1.getActive()).isEqualTo(0);
        assertThat(findDiary2.getActive()).isEqualTo(0);
        assertThat(findDiary3.getActive()).isEqualTo(0);
    }


}
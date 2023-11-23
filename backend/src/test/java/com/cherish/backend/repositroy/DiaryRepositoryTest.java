package com.cherish.backend.repositroy;

import com.cherish.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    BackUpRepository backUpRepository;

    Avatar avatar;

    BackUp backUp;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        backUp = BackUp.of("backup1","os1","device1",3,avatar);

        avatarRepository.save(avatar);
        backUpRepository.save(backUp);
    }

    @Test
    @DisplayName("여려개의 Diary entity가 데이터베이스에 저장되어있을 때 아바타 엔티티와 아이디와 백업 엔티티의 아이디로 조회가 가능해야 한다.")
    public void findDiariesByIdAndAvatarIdAndBackUpIdTest() throws Exception {
        //given
        Diary diary1 = Diary.of(DiaryKind.FREE, "title1", "content1", LocalDateTime.now(), "device1", "deviceid1", avatar, backUp);
        Diary diary2 = Diary.of(DiaryKind.FREE, "title2", "content2", LocalDateTime.now(), "device2", "deviceid2", avatar, backUp);
        Diary diary3 = Diary.of(DiaryKind.FREE, "title3", "content3", LocalDateTime.now(), "device3", "deviceid3", avatar, backUp);

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);
        diaryRepository.save(diary3);
        //when

        List<Diary> findDiaryList = diaryRepository.findDiariesByIdAndAvatarIdAndBackUpId(backUp.getId(), avatar.getId());
        //then

        assertThat(findDiaryList.size()).isEqualTo(3);
        assertThat(findDiaryList).extracting("id",String.class).contains(diary1.getId(),diary2.getId(),diary3.getId());
    }



}
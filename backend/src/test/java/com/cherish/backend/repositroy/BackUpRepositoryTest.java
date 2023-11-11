package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class BackUpRepositoryTest {

    @Autowired
    BackUpRepository backUpRepository;

    @Autowired
    AvatarRepository avatarRepository;

    Avatar avatar;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);
    }


    @Test
    @DisplayName("여려개의 BackUp Entity가 존재하는 경우 제일 최신의 데이터만 추출한다.")
    public void findBackUpByIdLatestTest() throws Exception {
        //given
        BackUp back1 = BackUp.of("testId1", "os1", "device1", 3, avatar);
        Thread.sleep(3000);
        BackUp back2 = BackUp.of("testId2", "os1", "device1", 3, avatar);
        Thread.sleep(3000);
        BackUp back3 = BackUp.of("testId2", "os1", "device1", 3, avatar);
        Thread.sleep(3000);

        backUpRepository.save(back1);
        backUpRepository.save(back2);
        backUpRepository.save(back3);
        //when
        Optional<BackUp> findBackUp = backUpRepository.findBackUpByIdLatest(avatar.getId());
        //then
        assertThat(findBackUp.get().getId()).isEqualTo(back3.getId());
    }

}
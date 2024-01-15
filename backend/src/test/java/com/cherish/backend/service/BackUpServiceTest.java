package com.cherish.backend.service;

import com.cherish.backend.controller.dto.response.BackUpHistoryResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.exception.NotExistBackUpException;
import com.cherish.backend.exception.NotExistBackUpHistoryException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class BackUpServiceTest {

    @Autowired
    BackUpService backUpService;

    @Autowired
    BackUpRepository backUpRepository;

    @Autowired
    AvatarRepository avatarRepository;

    Avatar avatar;

    BackUp backUp;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        backUp = BackUp.of("os1","device1",3,avatar);

        avatarRepository.save(avatar);
        backUpRepository.save(backUp);
    }

    @Test
    @DisplayName("만약 여러개의 백업이 존재하는 경우 가장 최신의 백업을 가지고 온다. ")
    public void getRecentBackUpTest() throws Exception {
        //given
        BackUp back1 = BackUp.of("os1", "device1", 3, avatar);
        Thread.sleep(3000);
        BackUp back2 = BackUp.of("os1", "device1", 3, avatar);
        Thread.sleep(3000);
        BackUp back3 = BackUp.of( "os1", "device1", 3, avatar);
        Thread.sleep(3000);

        backUpRepository.save(back1);
        backUpRepository.save(back2);
        backUpRepository.save(back3);
        //when
        BackUpHistoryResponse backUpHistoryResponse = backUpService.getRecentBackUp(avatar.getId());
        //then
        assertThat(backUpHistoryResponse.getBackUpID()).isEqualTo(back3.getId());
    }

    @Test
    @DisplayName("만약 백업이 존재하지 않는 경우 예외를 출력한다. ")
    public void getRecentBackUpNullTest() throws Exception {
        //given
        //when
        //then
        assertThrows(NotExistBackUpHistoryException.class,() -> backUpService.getRecentBackUp(Long.MAX_VALUE));
    }

}
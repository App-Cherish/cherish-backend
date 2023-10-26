package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AvatarRepositoryTest {

    Avatar avatar;

    @Autowired
    AvatarRepository avatarRepository;


    @BeforeEach
    public void init() {
        avatar = Avatar.builder()
                .name("test1")
                .gender(Gender.MALE)
                .birth(LocalDate.now()).build();
        avatarRepository.save(avatar);
    }

    @AfterEach
    public void destroy() {
        avatarRepository.deleteAll();
    }

    @Test
    @DisplayName("avatar id로 조회 시에 출력한 avatar의 avatar id와 일치해야 한다.")
    public void findAvatarTest() throws Exception {
        //given
        //when
        Optional<Avatar> findAvatar = avatarRepository.findAvatarById(avatar.getId());
        //then
        assertThat(findAvatar.get().getId()).isEqualTo(avatar.getId());
    }

    @Test
    @DisplayName("존재하지 않는 avatar id로 조회 시에 출력한 avatar는 null이어야 한다.")
    public void findAvatarNullTest() throws Exception {
        //given
        //when
        Optional<Avatar> findAvatar = avatarRepository.findAvatarById(Long.MAX_VALUE);
        //then
        assertThat(findAvatar.isEmpty()).isTrue();
    }



}
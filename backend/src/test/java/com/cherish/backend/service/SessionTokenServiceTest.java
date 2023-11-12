package com.cherish.backend.service;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SessionTokenServiceTest {


    @Autowired
    SessionTokenService sessionTokenService;

    @Autowired
    SessionTokenRepository sessionTokenRepository;

    @Autowired
    AvatarRepository avatarRepository;


    @Test
    @DisplayName("avatarId와 기기와 관련된 정보가 요청으로 들어오면 token을 정상적으로 출력한다.")
    public void createTokenTest() throws Exception {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("tokenSessionValue를 통해 세션토큰 발급을 요청하면 토큰을 정상적으로 출력한다.")
    public void tokenLoginTest() throws Exception {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("tokenSessionValue를 통해 세션토큰 발급을 요청하면 기존의 세션은 비활성화 된다.")
    public void tokenLoginDeActiveTest() throws Exception {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("로그인 한적 없는 deviceId로 토큰 세션 발급 요청을 한 경우 예외를 출력한다.")
    public void getTokenDeviceIdNull() throws Exception {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("로그인 한적 있는 deviceId로 토큰 세션 발급 요청을 한 경우 토큰을 출력한다.")
    public void getTokenDeviceIdTest() throws Exception {
        //given
        //when
        //then
    }

}
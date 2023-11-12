package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.AnotherLoginRequest;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.domain.*;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
public class AccountControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    SessionTokenRepository tokenRepository;

    MockHttpSession httpSession;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        httpSession = new MockHttpSession();
    }

    @AfterEach
    void clean() {
        httpSession.clearAttributes();
    }


    @Test
    @DisplayName("정상적인 회원가입 요청을 수행한 경우 http status 200과 세션, 세션용 토큰 쿠키를 생성한다.")
    public void signUpAPISuccessTest() throws Exception {
        //given
        String requestJson = "{\"oauthId\": \"testOauthId\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"}";
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isNotEmpty();
        //then
    }

    @Test
    @DisplayName("이미 가입되어있는 oauthId 및 플랫폼인 경우 회원가입을 진행할 경우 예외를 출력한다.")
    public void signUpAPIFailTestExistOauthId() throws Exception {
        //given
        String testOauthId = "testOauthId";
        Avatar avatar = Avatar.of("testId", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);
        accountRepository.save(Account.of(testOauthId, Platform.KAKAO, avatar));

        //when
        String requestJson = "{\"oauthId\": \"" + testOauthId + "\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"}";
        //when
        mockMvc.perform(post("/api/account/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest());
        //then
    }

    @Test
    @DisplayName("정상적인 로그인 요청을 수행한 경우 http status 200과 세션 쿠키를 생성한다.")
    public void loginAPISuccessTest() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(
                testOauthId1,
                Platform.KAKAO,
                avatar);

        accountRepository.save(account);
        LoginRequest loginRequest = new LoginRequest(testOauthId1, "iphone1234", "iphone15");
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }

    @Test
    @DisplayName("동일한 기기 내에서 이미 로그인한 기록이 있고 다른 플랫폼으로 로그인을 시도하였을때 만약 해당 플랫폼의 회원가입 기록이 없으면 다른 플랫폼의 회원가입을 자동으로 진행한다.")
    public void loginTestIfAnotherPlatformExistAccount() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String deviceId = "testDeviceId";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(
                testOauthId1,
                Platform.KAKAO,
                avatar
        ));
        SessionToken token = SessionToken.of(deviceId, "iphone15", avatar);
        tokenRepository.save(token);

        AnotherLoginRequest anotherLoginRequest = new AnotherLoginRequest(
                "testappleOauthId",
                deviceId,
                "iphone15",
                Platform.APPLE.getValue());
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post("/api/account/anotherplatformlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anotherLoginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.loginResponse.tokenId").exists())
                .andExpect(jsonPath("$.loginResponse.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }

    @Test
    @DisplayName("토큰을 이용해서 로그인을 진행 시에 정상적으로 요청이 들어온 경우 세션쿠키와 토큰을 정상적으로 내려준다.")
    public void tokenLoginSuccess() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String deviceId = "testDeviceId";

        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(
                testOauthId1,
                Platform.KAKAO,
                avatar
        ));
        SessionToken token = SessionToken.of(deviceId, "iphone15", avatar);
        tokenRepository.save(token);
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(get("/api/account/tokenlogin?token=" + token.getSessionTokenVaule()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }



}

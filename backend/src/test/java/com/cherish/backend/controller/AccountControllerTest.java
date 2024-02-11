package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.domain.*;
import com.cherish.backend.exception.SocialLoginValidationException;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.util.SocialLoginValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @MockBean
    Clock clock;

    @Autowired
    SessionTokenRepository sessionTokenRepository;

    @MockBean
    SocialLoginValidationUtil validationUtil;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        httpSession = new MockHttpSession();
        given(clock.instant()).willReturn(Clock.systemDefaultZone().instant());
        given(clock.getZone()).willReturn(Clock.systemDefaultZone().getZone());
    }

    @AfterEach
    void clean() {
        httpSession.clearAttributes();
    }


    @Test
    @DisplayName("1.정상적인 회원가입을 수행한 경우 http status 200과 세션과 바디에 토큰을 출력한다. ")
    public void signUpAPISuccessTest() throws Exception {
        //given
        String requestJson =
                "{\"oauthId\": \"testOauthId\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\" : \"asdasdasdas\"}";
        //when
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());
        MvcResult mvcResult = mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isNotEmpty();
        //then
    }

    @Test
    @DisplayName("2.정상적인 로그인 요청을 수행한 경우 http status 200과 세션과 바디에 토큰 값을 출력한다.")
    public void loginAPISuccessTest() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar);

        accountRepository.save(account);
        LoginRequest loginRequest = new LoginRequest(testOauthId1, Platform.KAKAO, "asdasdasdasdas", "iphone1234", "iphon15");
        //when
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());
        //then


        String request = "{\"oauthId\":\"testOauthId1\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";

        MvcResult mvcResult = mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists()).andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }

    @Test
    @DisplayName("3.이미 가입 되어있는 oauthId 및 플랫폼에 회원가입을 진행할 경우 http status 400을 출력한다.")
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
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\" : \"asdasdasdas\"}";
        //when
        mockMvc.perform(post("/api/account/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest());
        //then
    }


    @Test
    @DisplayName("4.oauth 로그인 시도 시에 만약 기존에 로그인한 기록이 없는 경우 300코드를 출력한다.")
    public void loginAPIFailTest2() throws Exception {
        //given
        String requestJson = "{\"oauthId\": \"test1234\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\" : \"asdasdasdas\"}";
        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin").contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(status().isMultipleChoices());
    }


    @Test
    @DisplayName("5.토큰 로그인 시도시에 정상적으로 요청을 수행한 경우 http status 200과 세션과 바디에 토큰 값을 출력한다.")
    public void tokenLoginSuccess() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String deviceId = "testDeviceId";

        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(
                testOauthId1,
                Platform.KAKAO, avatar));
        SessionToken token = SessionToken.of(deviceId, "iphone15", avatar);
        tokenRepository.save(token);
        //when
        //then
        TokenLoginRequest tokenLoginRequest = new TokenLoginRequest(token.getSessionTokenVaule());

        MvcResult mvcResult = mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenLoginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andExpect(jsonPath("$.expiredTime").exists())
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }

    @Test
    @DisplayName("6.토큰 로그인 시도시에 입력한 파라미터 값으로 토큰 값이 존재하지 않는 경우 상태코드 400을 출력한다.")
    public void tokenLoginFailTest1() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(post("/api/account/tokenlogin")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("7.토큰 로그인 시도시에 파라미터 값으로 들어온 토큰이 존재하지만 서버 내부에서 비 활성화된 토큰인 경우 상태코드 404를 출력한다.")
    public void tokenLoginFailTest2() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String deviceId = "testDeviceId";

        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(testOauthId1, Platform.KAKAO, avatar));
        SessionToken token = SessionToken.of(deviceId, "iphone15", avatar);
        token.deActive();
        tokenRepository.save(token);
        //when
        //then
        TokenLoginRequest tokenLoginRequest = new TokenLoginRequest(token.getSessionTokenVaule());
        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenLoginRequest))
                )
                .andExpect(status().isNotFound());

    }


    @Test
    @DisplayName("8.oauth 로그인 시에 정상적인 요청이 들어왔으나 OauthID는 일치하나 플랫폼이 일치하지 않는 경우 상태코드 300을 출력한다.")
    public void oauthLoginFailTest3() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar);

        accountRepository.save(account);

        String request = "{\"oauthId\":\"testOauthId3\",\"platform\": \"apple\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                ).andExpect(status().isMultipleChoices());
    }

    @Test
    @DisplayName("9.oauth 로그인 시에 정상적인 요청이 들어왔으나 플랫폼은 일치하나 oauthID가 일치하지 않는 경우 상태코드 300를 출력한다.")
    public void oauthLoginFailTest4() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar);

        accountRepository.save(account);

        String request = "{\"oauthId\":\"testOauthId3\",\"platform\": \"apple\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isMultipleChoices());
    }


    @Test
    @DisplayName("10.회원탈퇴 이후 일주일이 지나지 않은 상태에서 로그인을 진행하는 경우 상태코드 300을 출력한다.")
    public void oauthLoginFailTest6() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(
                testOauthId1,
                Platform.KAKAO, avatar);

        accountRepository.save(account);
        //when
        //then
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());
        accountService.leave(avatar.getId());

        String request = "{\"oauthId\":\"testOauthId1\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";

        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isMultipleChoices());
    }

    @Test
    @DisplayName("11.회원탈퇴 이후 일주일이 지난 상태에서 로그인을 진행하는 경우 상태코드 404을 출력한다.")
    public void oauthLoginFailTest5() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(
                testOauthId1,
                Platform.KAKAO, avatar);

        accountRepository.save(account);

        accountService.leave(avatar.getId());

        String request = "{\"oauthId\":\"testOauthId1\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        //when
        Clock fixedClock = Clock.fixed(ZonedDateTime.now().minusDays(8).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
        //then

        mockMvc.perform(post("/api/account/oauthlogin").contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isMultipleChoices());
    }


    @Test
    @DisplayName("12.회원탈퇴 시에는 세션 값 및 세션 토큰값을 전부 비활성화 하여야 한다.")
    public void leaveTest() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(
                testOauthId1,
                Platform.KAKAO, avatar);

        accountRepository.save(account);
        SessionToken sessionToken = SessionToken.of("device1", "device15", avatar);
        sessionTokenRepository.save(sessionToken);
        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());
        //when
        //then
        mockMvc.perform(get("/api/account/leave").session(httpSession)).andExpect(status().isOk());

        assertThrows(IllegalStateException.class, () -> httpSession.getAttribute(ConstValue.sessionName).toString());
        assertThat(sessionToken.getActive()).isEqualTo(0);
    }

    @Test
    @DisplayName("13.로그아웃 시도시에 session값이 만료되고 토큰 값도 만료되어야 한다.")
    public void logoutSuccessTest() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String deviceId = "device1234";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(
                testOauthId1,
                Platform.KAKAO,
                avatar
        ));
        SessionToken token = SessionToken.of(deviceId, "iphone15", avatar);
        tokenRepository.save(token);

        httpSession.setAttribute(ConstValue.sessionName,avatar.getId());
        //when
        //then
        mockMvc.perform(get("/api/account/logout?token=" + token.getSessionTokenVaule())
                        .session(httpSession))
                .andExpect(status().isOk());

        assertThrows(IllegalStateException.class, () -> httpSession.getAttribute(ConstValue.sessionName).toString());
    }

    @Test
    @DisplayName("14. 회원 가입 시에 소셜로그인 서버와 ID와 요청으로 들어온 ID값이 다른 경우 상태코드 400을 출력한다.")
    public void loginFailTestIfIdValueIsDiff() throws Exception {
        //given

        doThrow(SocialLoginValidationException.class).when(validationUtil).validation(anyString(), anyString(), any());
        String requestJson = "{\"oauthId\": \"test1234\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\" : \"asdasdasdas\"}";
        //when
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

    }


}

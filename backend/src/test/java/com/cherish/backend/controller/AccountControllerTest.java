package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.KakaoLoginRequest;
import com.cherish.backend.controller.dto.request.KakaoSignUpRequest;
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

    private String kakaoLoginRequestToString(KakaoLoginRequest kakaoLoginRequest) {
        return "{" +
                "\"oauthId\":\"" + kakaoLoginRequest.getOauthId() + "\"," +
                "\"osVersion\":\"" + kakaoLoginRequest.getOsVersion() + "\"," +
                "\"deviceType\":\"" + kakaoLoginRequest.getDeviceType() + "\"," +
                "\"platform\":\"" + kakaoLoginRequest.getPlatform() + "\"," +
                "\"accessToken\":\"" + kakaoLoginRequest.getAccessToken() + "\"," +
                "\"refreshToken\":\"" + kakaoLoginRequest.getRefreshToken() + "\"" +
                "}";
    }

    private String kakaoSignUpRequestToString(KakaoSignUpRequest kakaoSignUpRequest) {
        return "{" +
                "\"oauthId\":\"" + kakaoSignUpRequest.getOauthId() + "\"," +
                "\"name\":\"" + kakaoSignUpRequest.getName() + "\"," +
                "\"birth\": \"" + kakaoSignUpRequest.getBirth() + "\"," +
                "\"platform\": \"" + kakaoSignUpRequest.getPlatfrom() + "\"," +
                "\"gender\" : \"" + kakaoSignUpRequest.getGender().getValue() + "\"," +
                "\"osVersion\" : \"" + kakaoSignUpRequest.getOsVersion() + "\"," +
                "\"deviceType\": \"" + kakaoSignUpRequest.getDeviceType() + "\"," +
                "\"accessToken\" : \"" + kakaoSignUpRequest.getAccessToken() + "\"," +
                "\"refreshToken\" : \"" + kakaoSignUpRequest.getRefreshToken() + "\"" +
                "}";
    }


    @Test
    @DisplayName("1.정상적인 회원가입을 수행한 경우 http status 200과 세션과 바디에 토큰을 출력한다. ")
    public void signUpAPISuccessTest() throws Exception {

        String requestJson = kakaoSignUpRequestToString(new KakaoSignUpRequest("oauthId1", "name1", "kakao", LocalDate.now(), Gender.FEMALE, "osVersion", "deviceType", "accessToken1", "refreshToken1"));

        //given
        //when
        doNothing().when(validationUtil).kakaoLoginValidation(anyString(), anyString());
        MvcResult mvcResult = mockMvc.perform(post("/api/account/signup/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
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
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

        accountRepository.save(account);
        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(testOauthId1, "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");
        //when
        doNothing().when(validationUtil).kakaoLoginValidation(anyString(), anyString());
        //then


        String request = kakaoLoginRequestToString(kakaoLoginRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/account/oauth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").exists())
                .andReturn();

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
        accountRepository.save(Account.of(testOauthId, Platform.KAKAO, avatar, "asdasdasdasdasd"));


        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(testOauthId, "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        //when
        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);
        //when

        mockMvc.perform(post("/api/account/signup/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest());
        //then
    }


    @Test
    @DisplayName("4.oauth 로그인 시도 시에 만약 기존에 로그인한 기록이 없는 경우 300코드를 출력한다.")
    public void loginAPIFailTest2() throws Exception {
        //given
        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("test1", "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);
        //when
        //then
        mockMvc.perform(post("/api/account/oauth/kakao").contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("5.토큰 로그인 시도시에 정상적으로 요청을 수행한 경우 http status 200과 세션과 바디에 토큰 값을 출력한다.")
    public void tokenLoginSuccess() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String osVersion = "testosVersion";

        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd"));
        SessionToken token = SessionToken.of(osVersion, "iphone15", avatar);
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
                .andReturn();

        String sessionValue = mvcResult.getRequest().getSession().getAttribute(ConstValue.sessionName).toString();
        assertThat(sessionValue).isEqualTo(avatar.getId().toString());
    }

    @Test
    @DisplayName("6.토큰 로그인 시도시에 입력한 파라미터 값으로 토큰 값이 존재하지 않는 경우 상태코드 404을 출력한다.")
    public void tokenLoginFailTest1() throws Exception {
        //given
        //when
        //then
        TokenLoginRequest tokenLoginRequest = new TokenLoginRequest("asdasdasdasdasdasd");

        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenLoginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("7.토큰 로그인 시도시에 파라미터 값으로 들어온 토큰이 존재하지만 서버 내부에서 비 활성화된 토큰인 경우 상태코드 404를 출력한다.")
    public void tokenLoginFailTest2() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        String osVersion = "testosVersion";

        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd"));
        SessionToken token = SessionToken.of(osVersion, "iphone15", avatar);
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
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

        accountRepository.save(account);

        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("test1", "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);
        //when
        //then
        mockMvc.perform(post("/api/account/oauth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("9.oauth 로그인 시에 정상적인 요청이 들어왔으나 플랫폼은 일치하나 oauthID가 일치하지 않는 경우 상태코드 300를 출력한다.")
    public void oauthLoginFailTest4() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

        accountRepository.save(account);

        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("test1", "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);
        //when
        //then
        mockMvc.perform(post("/api/account/oauth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("10.회원탈퇴 이후 일주일이 지나지 않은 상태에서 로그인을 진행하는 경우 상태코드 300을 출력한다.")
    public void oauthLoginFailTest6() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

        accountRepository.save(account);
        //when
        //then
        doNothing().when(validationUtil).kakaoLoginValidation(anyString(), anyString());
        accountService.leave(avatar.getId());

        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("test1", "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);

        mockMvc.perform(post("/api/account/oauth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("11.회원탈퇴 이후 일주일이 지난 상태에서 로그인을 진행하는 경우 상태코드 404을 출력한다.")
    public void oauthLoginFailTest5() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

        accountRepository.save(account);

        accountService.leave(avatar.getId());

        KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("test1", "accessToken", "kakao", "refreshToken", "iphone1234", "iphon15");

        String requestJson = kakaoLoginRequestToString(kakaoLoginRequest);
        //when
        Clock fixedClock = Clock.fixed(ZonedDateTime.now().minusDays(8).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
        //then

        mockMvc.perform(post("/api/account/oauth/kakao").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("12.회원탈퇴 시에는 세션 값 및 세션 토큰값을 전부 비활성화 하여야 한다.")
    public void leaveTest() throws Exception {
        //given
        String testOauthId1 = "testOauthId1";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        Account account = Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd");

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
        String osVersion = "device1234";
        Avatar avatar = Avatar.of("testOauthName1", LocalDate.now(), Gender.MALE);
        accountRepository.save(Account.of(testOauthId1, Platform.KAKAO, avatar, "asdasdasdasdasd"));
        SessionToken token = SessionToken.of(osVersion, "iphone15", avatar);
        tokenRepository.save(token);

        httpSession.setAttribute(ConstValue.sessionName, avatar.getId());
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

        doThrow(SocialLoginValidationException.class).when(validationUtil).kakaoLoginValidation(anyString(), anyString());
        String requestJson = kakaoSignUpRequestToString(new KakaoSignUpRequest("oauthId", "name1", "kakao", LocalDate.now(), Gender.MALE, "device1", "deviceType", "accessToken1", "refreshToken1"));
        //when
        mockMvc.perform(post("/api/account/signup/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

    }


}

package com.cherish.backend.controller.docs;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.ExistOauthIdException;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import com.cherish.backend.service.dto.TokenCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriPort = 80, uriHost = "api.cherishkr.com", uriScheme = "https")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AccountControllerDocs {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession session = new MockHttpSession();

    @MockBean
    AccountService accountService;

    @MockBean
    SessionTokenService sessionTokenService;


    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 성공한 요청")
    public void loginDocsSuccess() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginDto.class))).willReturn(1L);
        given(sessionTokenService.createToken(anyLong(), any(TokenCreateDto.class))).willReturn(SessionToken.of("testdeviceId", "testType", any(Avatar.class)));
        LoginRequest loginRequest = new LoginRequest("testId", "iphone1234", "iphone15");
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andDo(document("oauthLogin",
                        responseHeaders(
                                headerWithName("Set-Cookie").description("세션 및 세션 발급 용 쿠키, \n SESSION과 sessiontoken으로 총 두개")
                        ),
                        requestFields(
                                fieldWithPath("oauthId").type(JsonFieldType.STRING).description("oauthId를 넣어주세요(카카오 애플 상관없이)"),
                                fieldWithPath("deviceId").type(JsonFieldType.STRING).description("deviceId를 입력해주세요."),
                                fieldWithPath("deviceType").type(JsonFieldType.STRING).description("deviceType을 입력해주세요.")
                        )))
                .andDo(print())
                .andReturn();


    }

    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 실패한 요청 - account가 없는 경우")
    public void loginDocsFail1() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginDto.class))).willThrow(new NotExistAccountException());
        LoginRequest loginRequest = new LoginRequest("testId", "iphone1234", "iphone15");
        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isNotFound())
                .andDo(document("oauthLoginFail1",
                        preprocessRequest(prettyPrint())
                ));
    }

    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 실패한 요청 - 서버 내부 오류")
    public void loginDocsFail2() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginDto.class))).willThrow(new IllegalStateException("존재하지 않는 avatarId 입니다."));
        LoginRequest loginRequest = new LoginRequest("testId", "iphone1234", "iphone15");
        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(document("oauthLoginFail2",
                        preprocessRequest(prettyPrint())
                ));
    }

    @Test
    @DisplayName("[DOCS]token 로그인 성공")
    public void tokenLoginDocsSuccess() throws Exception {
        //given
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));
        given(sessionTokenService.tokenLogin(any(String.class))).willReturn(sessionToken);
        //when
        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(ConstValue.tokenCookieName, sessionToken.getSessionTokenVaule()))
                )
                .andExpect(status().isOk())
                .andDo(document("tokenLoginSuccess",
                        responseHeaders(
                                headerWithName("Set-Cookie").description("세션 및 세션 발급 용 쿠키, \n SESSION과 sessiontoken으로 총 두개")
                        ),
                        requestCookies(
                                cookieWithName("sessiontoken").description("헤더에 쿠키에 올려서 넣어주시면 됩니다. 기존에 받은 sessiontoken의 값을 쿠키의 값으로 함께 넣어주면 됩니다!")
                        )
                ));
        //then
    }

    @Test
    @DisplayName("[DOCS]token로그인 실패1")
    public void tokenLoginFailTest1() throws Exception {
        //given
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));
        given(sessionTokenService.tokenLogin(any(String.class))).willThrow(new IllegalStateException("존재하지 않는 토큰입니다."));
        //when
        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(ConstValue.tokenCookieName, UUID.randomUUID().toString()))
                )
                .andExpect(status().isBadRequest())
                .andDo(document("tokenLoginFail1",
                        preprocessRequest(prettyPrint())
                ));
    }

    @Test
    @DisplayName("[DOCS]token로그인 실패2")
    public void tokenLoginFailTest2() throws Exception {
        //given
        //when
        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(document("tokenLoginFail2",
                        preprocessRequest(prettyPrint())
                ));
        //then
    }

    @Test
    @DisplayName("[DOCS]정상적이 요청이 들어왔을시에 회원가입 성공 ")
    public void signUpSuccessTest() throws Exception {
        //given
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));
        String requestJson = "{\"oauthId\": \"" + "test1234" + "\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"}";
        given(accountService.signUp(any(SignUpDto.class))).willReturn(1L);
        given(sessionTokenService.createToken(eq(1L), any(TokenCreateDto.class))).willReturn(sessionToken);
        //when
        //then
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isOk())
                .andDo(document("signUpSuccess",
                        responseHeaders(
                                headerWithName("Set-Cookie").description("세션 및 세션 발급 용 쿠키, \n SESSION과 sessiontoken으로 총 두개")
                        ),
                        requestFields(
                                fieldWithPath("oauthId").type(JsonFieldType.STRING).description("oauthId를 넣어주세요(카카오 애플 상관없이)"),
                                fieldWithPath("platform").type(JsonFieldType.STRING).description("oauthId를 넣어주세요(kakao or apple : 소문자 입니다.)"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자의 이름을 넣어주세요."),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("사용자의 생일을 넣어주세요"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("성별을 넣어주세요(mail or female : 소문자 입니다.)"),
                                fieldWithPath("deviceId").type(JsonFieldType.STRING).description("deviceId를 입력해주세요."),
                                fieldWithPath("deviceType").type(JsonFieldType.STRING).description("deviceType을 입력해주세요.")
                        )));
    }

    @Test
    @DisplayName("[DOCS] 회원가입 실패 테스트 - 이미 존재하는 OauthID ")
    public void signUpFailTest1() throws Exception {
        //given
        String requestJson = "{\"oauthId\": \"" + "test1234" + "\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"}";
        given(accountService.signUp(any(SignUpDto.class))).willThrow(ExistOauthIdException.class);
        //when
        //then
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(document("signUpFailTest1",
                        preprocessRequest(prettyPrint())
                ));
    }

    @Test
    @DisplayName("[DOCS] 회원가입 실패 테스트 - 이미 존재하지 않는 아바타 ID ")
    public void signUpFailTest2() throws Exception {
        //given
        String requestJson = "{\"oauthId\": \"" + "test1234" + "\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"}";
        given(accountService.signUp(any(SignUpDto.class))).willReturn(1L);
        given(sessionTokenService.createToken(eq(1L), any(TokenCreateDto.class))).willThrow(new IllegalStateException("존재하지 않는 avatarId 입니다."));
        //when
        //then
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(document("signUpFailTest2",
                        preprocessRequest(prettyPrint())
                ));
    }


}

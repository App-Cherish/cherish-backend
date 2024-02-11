package com.cherish.backend.controller.docs;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.LeaveAccountStoreException;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.CreateTokenDto;
import com.cherish.backend.util.SocialLoginValidationUtil;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
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

    @Autowired
    AvatarRepository avatarRepository;

    @MockBean
    SocialLoginValidationUtil validationUtil;

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
        session = new MockHttpSession();
    }


    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 성공한 요청")
    public void loginDocsSuccess() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginRequest.class))).willReturn(1L);
        given(sessionTokenService.createToken(any(CreateTokenDto.class))).willReturn(new LoginResponse("tokeIdexample", LocalDateTime.now()));
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());
        String request = "{\"oauthId\":\"testId\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";

        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andDo(document("oauth 성공하는 경우",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("OAUTH ID로 로그인 하기")
                                .description("oauth id을 통한 로그인 성공을 하는 경우입니다.")
                                .requestSchema(Schema.schema("OAUTH LOGIN Scheme"))
                                .requestFields(
                                        fieldWithPath("oauthId").type("String").description("해당 플랫폼에서 발급 받은 oauth ID 값을 넣어주세요."),
                                        fieldWithPath("deviceId").type("String").description("해당 디바이스의 고유값을 입력해주세요.(식별값)"),
                                        fieldWithPath("accessToken").type("String").description("소셜 로그인시에 받은 access Token을 넣어주세요."),
                                        fieldWithPath("deviceType").type("String").description("해당 디바이스의 기기 명을 입력해주세요(EX:IPhone15)"),
                                        fieldWithPath("platform").type("String").description("oauth로그인을 진행한 플랫폼을 입력해주세요(kakao or apple)")
                                        )
                                .build())));

    }

    @Test
    @DisplayName("[DOCS]oauth 로그인 회원 탈퇴 이후 일주일 이내")
    public void loginDocsActivateSuccess() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginRequest.class))).willThrow(LeaveAccountStoreException.class);
        String request = "{\"oauthId\":\"testId\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());

        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isMultipleChoices())
                .andDo(document("oauth 회원탈퇴 이후 일주일 이내인 경우",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("OAUTH ID로 로그인 하기")
                                .description("oauth id을 통한 로그인 성공을 하는 경우입니다.")
                                .requestSchema(Schema.schema("OAUTH LOGIN Scheme"))
                                .requestFields(
                                        fieldWithPath("oauthId").type("String").description("해당 플랫폼에서 발급 받은 oauth ID 값을 넣어주세요."),
                                        fieldWithPath("deviceId").type("String").description("해당 디바이스의 고유값을 입력해주세요.(식별값)"),
                                        fieldWithPath("accessToken").type("String").description("소셜 로그인시에 받은 access Token을 넣어주세요."),
                                        fieldWithPath("deviceType").type("String").description("해당 디바이스의 기기 명을 입력해주세요(EX:IPhone15)"),
                                        fieldWithPath("platform").type("String").description("oauth로그인을 진행한 플랫폼을 입력해주세요(kakao or apple)")
                                )
                                .build())));

    }

    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 실패한 요청 - account가 없는 경우, 플랫폼이 다른 경우, 회원탈퇴 이후 일주일 인 경우")
    public void loginDocsFail1() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginRequest.class))).willThrow(NotExistAccountException.class);
        String request = "{\"oauthId\":\"testOauthIdnone\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());

        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isMultipleChoices())
                .andDo(document("oauth 실패하는 경우 - 입력한 OAuth ID가 없는 경우, 플랫폼이 다른 경우",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("OAUTH ID로 로그인 하기")
                                .description("oauth id을 통한 로그인 실패을 하는 경우입니다.")
                                .requestSchema(Schema.schema("OAUTH LOGIN Scheme"))
                                .build()
                        )));
    }

    @Test
    @DisplayName("[DOCS]oauth 로그인 기능 실패한 요청 - 회원탈퇴 이후 일주일 이내인 경우")
    public void loginDocsPlatformFail1() throws Exception {
        //given
        given(accountService.oauthLogin(any(LoginRequest.class))).willThrow(new LeaveAccountStoreException());
        String request = "{\"oauthId\":\"testId\",\"platform\": \"kakao\",\"accessToken\":\"asdasdasdasdas\",\"deviceId\":\"iphone1234\",\"deviceType\":\"iphon15\"}";
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());

        //when
        //then
        mockMvc.perform(post("/api/account/oauthlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isMultipleChoices())
                .andDo(document("oauth 계정 삭제 후 복구 - 계정 삭제 후 일주일 이내 인 경우",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("OAUTH ID로 로그인 하기")
                                .description("oauth id을 통한 로그인 실패을 하는 경우입니다.")
                                .requestSchema(Schema.schema("OAUTH LOGIN Scheme"))
                                .build()
                        )));
    }

    @Test
    @DisplayName("[DOCS]token 로그인 성공")
    public void tokenLoginDocsSuccess() throws Exception {
        //given
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));
        TokenLoginRequest tokenLoginRequest = new TokenLoginRequest(sessionToken.getSessionTokenVaule());
        given(sessionTokenService.tokenLogin(eq(tokenLoginRequest))).willReturn(new LoginResponse(sessionToken.getSessionTokenVaule(), sessionToken.getExpired_date()));

        //when
        mockMvc.perform(post("/api/account/tokenlogin")
                        .content(objectMapper.writeValueAsString(tokenLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())
                .andDo(document("토큰으로 로그인 성공1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("Token으로 로그인해서 세션 쿠키 발급 받기")
                                .description("토큰 로그인 성공하는 경우 입니다..")
                                .requestFields(
                                        fieldWithPath("token").type("String").description("발급 받은 토큰 값을 입력해주세요")
                                        ).build())
                ));
    }

    @Test
    @DisplayName("[DOCS]token로그인 실패1")
    public void tokenLoginFailTest1() throws Exception {
        //given
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));
        TokenLoginRequest tokenLoginRequest = new TokenLoginRequest(sessionToken.getSessionTokenVaule());
        given(sessionTokenService.tokenLogin(eq(tokenLoginRequest))).willThrow(new IllegalStateException("존재하지 않는 토큰입니다."));
        //when
        mockMvc.perform(post("/api/account/tokenlogin")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(document("토큰 로그인 존재하지 않는 토큰이였던 경우 - 실패케이스1",
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("Token으로 로그인해서 세션 쿠키 발급 받기")
                                .description("토큰 로그인 실패하는 경우1 입니다.")
                                .build())));
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
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\": \"카카오에서 발급받은 아이디\"" +
                "}";

        given(accountService.signUp(any(SignUpRequest.class))).willReturn(1L);
        given(sessionTokenService.createToken(any(CreateTokenDto.class))).willReturn(new LoginResponse(sessionToken.getSessionTokenVaule(), sessionToken.getExpired_date()));
        doNothing().when(validationUtil).validation(anyString(), anyString(), any());

        //when
        //then
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("회원가입 성공",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("회원가입 성공")
                                .description("정상적인 요청으로 회원가입이 성공하는 경우")
                                .requestSchema(Schema.schema("Sign Up Scheme"))
                                                .
                                                requestFields(
                                                        fieldWithPath("oauthId").type("String").description("oauthId를 넣어주세요(카카오 애플 상관없이)"),
                                                        fieldWithPath("platform").type("String").description("oauthId를 넣어주세요(kakao or apple : 소문자 입니다.)"),
                                                        fieldWithPath("name").type("String").description("사용자의 이름을 넣어주세요."),
                                                        fieldWithPath("birth").type("String").description("사용자의 생일을 넣어주세요 (yyyy-mm-dd) ex)1970-12-31"),
                                                        fieldWithPath("gender").type("String").description("성별을 넣어주세요(mail or female : 소문자 입니다.)"),
                                                        fieldWithPath("deviceId").type("String").description("deviceId를 입력해주세요."),
                                                        fieldWithPath("deviceType").type("String").description("deviceType을 입력해주세요."),
                                                        fieldWithPath("accessToken").type("String").description("oauth로그인 서버에서 받은 id값을 입력해주세요.")
                                                )
                                                .build())));
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
        SessionToken sessionToken = SessionToken.of("testId", "testDevice", Avatar.of("testName", LocalDate.now(), Gender.MALE));


        given(accountService.signUp(any(SignUpRequest.class))).willThrow(new IllegalArgumentException("이미 존재하는 oauthId입니다."));
        given(sessionTokenService.createToken(any(CreateTokenDto.class))).willReturn(new LoginResponse(sessionToken.getSessionTokenVaule(), sessionToken.getExpired_date()));

        //when
        //then
        mockMvc.perform(post("/api/account/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("회원가입이 실패하는 경우 - 이미 가입된 oauth ID",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("회원가입 실패")
                                .description("이미 존재하는 oauthId로 회원가입을 요청한 경우")
                                .requestSchema(Schema.schema("Sign Up Scheme"))
                                .build())
                ));
    }

    @Test
    @DisplayName("[DOCS] 로그아웃")
    public void LogoutTest() throws Exception {
        //given
        session.setAttribute(ConstValue.sessionName, 1L);
        //when
        //then
        mockMvc.perform(get("/api/account/logout?token=" + "EXTOKENVALUE")
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(document("로그아웃 하는 경우",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("로그아웃")
                                .description("헤더에 세션 쿠키값을 넣어주세요.")
                                .queryParameters(parameterWithName("token").type(SimpleType.STRING).description("발급 받은 토큰을 parameter에 입력해주세요."))
                                .build())
                ));
    }

    @Test
    @DisplayName("[DOCS]회원탈퇴")
    public void leaveTest() throws Exception {
        //given

        session.setAttribute(ConstValue.sessionName,1L);

        doNothing().when(accountService).leave(anyLong());
        //when
        //then
        mockMvc.perform(get("/api/account/leave")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(document("회원탈퇴 성공(헤더에 세션을 쿠키로 포함해서 보내주셔야 합니다.)",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("회원탈퇴 성공(헤더에 세션을 쿠키로 포함해서 보내주셔야 합니다.)")
                                .description("회원탈퇴 API 입니다.")
                                .build()
                        )));
    }

    @Test
    @DisplayName("[DOCS] 회원탈퇴 계정 복구하기 성공하는 경우")
    public void ActivateTest() throws Exception {
        //given
        session.setAttribute(ConstValue.sessionName,1L);
        given(accountService.oauthLogin(any(LoginRequest.class))).willReturn(1L);
        given(sessionTokenService.createToken(any(CreateTokenDto.class))).willReturn(new LoginResponse("asdasdasd", LocalDateTime.now()));
        String requestJson = "{\"oauthId\": \"" + "test1234" + "\"," +
                "\"name\":\"testid\"," +
                "\"platform\":\"kakao\"," +
                "\"birth\": \"2022-10-23\"," +
                "\"gender\" : \"male\", " +
                "\"deviceId\" : \"iphoneId\"," +
                "\"deviceType\": \"ihpone15\"," +
                "\"accessToken\": \"카카오에서 발급받은 아이디\"" +
                "}";
        //when
        //test
        mockMvc.perform(post("/api/account/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("회원탈퇴 된 계정을 복구",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("ACCOUNT API")
                                .summary("탈퇴된 계정 복구")
                                .description("oauthId 값을 통해 계정을 복구합니다. ")
                                .build()
                        )));
    }




}

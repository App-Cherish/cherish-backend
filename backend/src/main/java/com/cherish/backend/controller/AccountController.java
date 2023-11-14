package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.AnotherLoginRequest;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
import com.cherish.backend.controller.dto.response.AnotherPlatformResponse;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.NotFountTokenException;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.AnotherPlatformSignUpDto;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import com.cherish.backend.service.dto.TokenCreateDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SessionTokenService sessionTokenService;

    @PostMapping("/oauthlogin")
    public LoginResponse oauthLogin(@RequestBody LoginRequest request, HttpSession session) {
        Long avatarId = accountService.oauthLogin(new LoginDto(request.getOauthId(), request.getDeviceId(), request.getDeviceType()));
        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(request.getDeviceId(), request.getDeviceType()));
        extractedSession(session, avatarId);
        return new LoginResponse(token.getSessionTokenVaule(), token.getExpired_date());
    }

    @GetMapping("/tokenlogin")
    public LoginResponse tokenLogin(@RequestParam(name = "token") String value, HttpSession session) {

        if (value == null) {
            throw new NotFountTokenException();
        }
        SessionToken token = sessionTokenService.tokenLogin(value);
        extractedSession(session,token.getAvatar().getId());

        return new LoginResponse(token.getSessionTokenVaule(),token.getExpired_date());
    }

    @PostMapping("/anotherplatformlogin")
    public AnotherPlatformResponse anotherPlatformLogin(@RequestBody AnotherLoginRequest request, HttpSession session) {
        SessionToken findToken = sessionTokenService.getTokenByDeviceId(request.getDeviceId());

        Long avatarId = accountService.anotherPlatformSignUp(new AnotherPlatformSignUpDto(
                findToken.getAvatar().getId(),
                request.getOauthId(),
                getPlatform(request.getPlatform()),
                request.getDeviceId())
        );

        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(request.getDeviceId(), request.getDeviceType()));
        extractedSession(session, avatarId);

        return new AnotherPlatformResponse("기존에 등록되어있는 아이디로 로그인이 되었습니다.", new LoginResponse(token.getSessionTokenVaule(), token.getExpired_date()));
    }

    @PostMapping("/signup")
    public LoginResponse signUp(@RequestBody SignUpRequest signUpRequest, HttpSession session) {

        Platform platform = getPlatform(signUpRequest.getPlatform());
        Gender gender = getGender(signUpRequest.getGender());

        Long avatarId = accountService.signUp(new SignUpDto(
                signUpRequest.getOauthId(),
                platform,
                signUpRequest.getName(),
                signUpRequest.getBirth(),
                gender,
                signUpRequest.getDeviceId()));
        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(signUpRequest.getDeviceId(), signUpRequest.getDeviceType()));
        extractedSession(session, avatarId);
        return new LoginResponse(token.getSessionTokenVaule(), token.getExpired_date());
    }

    public void extractedSession(HttpSession session, Long avatarId) {
        session.setAttribute(ConstValue.sessionName, avatarId);
        session.setMaxInactiveInterval(3600);
    }


    private Platform getPlatform(String platform) {
        if (platform.equals(Platform.APPLE.getValue())) {
            return Platform.APPLE;
        }

        if (platform.equals(Platform.KAKAO.getValue())) {
            return Platform.KAKAO;
        }

        throw new IllegalArgumentException("잘못된 플랫폼 요청 값 입니다.");
    }

    private Gender getGender(String gender) {
        if (gender.equals(Gender.MALE.getValue())) {
            return Gender.MALE;
        }

        if (gender.equals(Gender.FEMALE.getValue())) {
            return Gender.FEMALE;
        }

        throw new IllegalArgumentException("잘못된 성별 요청 값  입니다.");
    }


}

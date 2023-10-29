package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.AnotherLoginRequest;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SessionTokenService sessionTokenService;

    @PostMapping("/oauthlogin")
    public void oauthLogin(@RequestBody LoginRequest request, HttpServletResponse servletResponse, HttpSession session) {
        Long avatarId = accountService.oauthLogin(new LoginDto(request.getOauthId(), request.getDeviceId(), request.getDeviceType()));
        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(request.getDeviceId(), request.getDeviceType()));
        extractedCookie(servletResponse, session, avatarId, token);
    }

    @PostMapping("/tokenlogin")
    public void tokenLogin(@CookieValue(value = ConstValue.tokenCookieName, required = false) Cookie cookie, HttpServletResponse servletResponse, HttpSession session) {
        if (cookie == null) {
            throw new NotFountTokenException();
        }

        String tokenValue = cookie.getValue();
        SessionToken token = sessionTokenService.tokenLogin(tokenValue);
        extractedCookie(servletResponse, session, token.getAvatar().getId(), token);
    }

    @PostMapping("/anotherplatformlogin")
    public void anotherPlatformLogin(@RequestBody AnotherLoginRequest request, HttpServletResponse servletResponse, HttpSession session) {
        SessionToken findToken = sessionTokenService.getTokenByDeviceId(request.getDeviceId());

        Long avatarId = accountService.anotherPlatformSignUp(new AnotherPlatformSignUpDto(
                findToken.getAvatar().getId(),
                request.getOauthId(),
                getPlatform(request.getPlatform()))
        );

        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(request.getDeviceId(), request.getDeviceType()));
        extractedCookie(servletResponse, session, avatarId, token);
    }


    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest signUpRequest, HttpServletResponse servletResponse, HttpSession session) {

        Platform platform = getPlatform(signUpRequest.getPlatform());
        Gender gender = getGender(signUpRequest.getGender());

        Long avatarId = accountService.signUp(new SignUpDto(
                signUpRequest.getOauthId(),
                platform,
                signUpRequest.getName(),
                signUpRequest.getBirth(),
                gender));
        SessionToken token = sessionTokenService.createToken(avatarId, new TokenCreateDto(signUpRequest.getDeviceId(), signUpRequest.getDeviceType()));
        extractedCookie(servletResponse, session, avatarId, token);
    }

    private void extractedTokenCookie(HttpServletResponse servletResponse, String tokenValue) {
        Cookie tokenCookie = new Cookie(ConstValue.tokenCookieName, tokenValue);
        tokenCookie.setPath("/");

        tokenCookie.setMaxAge(60 * 60 * 24 * 30);

        servletResponse.addCookie(tokenCookie);
    }

    public void extractedSession(HttpSession session, Long avatarId) {
        session.setAttribute(ConstValue.sessionName, avatarId);
        session.setMaxInactiveInterval(3600);
    }

    private void extractedCookie(HttpServletResponse servletResponse, HttpSession session, Long avatarId, SessionToken token) {
        extractedSession(session, avatarId);
        extractedTokenCookie(servletResponse, token.getSessionTokenVaule());
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

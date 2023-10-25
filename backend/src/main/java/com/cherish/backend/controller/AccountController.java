package com.cherish.backend.controller;

import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import com.cherish.backend.service.dto.TokenDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SessionTokenService sessionTokenService;


    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request, HttpServletResponse servletResponse, HttpSession session) {

        Long avatarId = accountService.login(new LoginDto(request.getOauthId(), request.getDeviceId(), request.getDeviceType()));
        session.setAttribute("avatarId",avatarId);
        session.setMaxInactiveInterval(3600);

        SessionToken token = sessionTokenService.getToken(avatarId, new TokenDto(request.getDeviceId(), request.getDeviceType()));

        extractedTokenCookie(servletResponse, token.getSessionTokenVaule());
    }

    private void extractedTokenCookie(HttpServletResponse servletResponse, String tokenValue) {
        Cookie tokenCookie = new Cookie("sessionToken", tokenValue);
        tokenCookie.setPath("/");

        tokenCookie.setMaxAge(60*60*24*30);

        servletResponse.addCookie(tokenCookie);
    }


    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest signUpRequest, HttpServletResponse servletResponse, HttpSession session) {

        Platform platform = getPlatform(signUpRequest.getPlatform());
        Gender gender = getGender(signUpRequest.getGender());

        Long avatarId = accountService.signUp(new SignUpDto(signUpRequest.getOauthId(),
                platform,
                signUpRequest.getName(),
                signUpRequest.getBirth(),
                gender));

        session.setAttribute("avatarId",avatarId);


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

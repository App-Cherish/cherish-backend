package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.AppleSignUpRequest;
import com.cherish.backend.controller.dto.request.KakaoLoginRequest;
import com.cherish.backend.controller.dto.request.KakaoSignUpRequest;
import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.domain.Platform;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.CreateTokenDto;
import com.cherish.backend.util.SocialLoginValidationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SessionTokenService sessionTokenService;
    private final SocialLoginValidationUtil validationUtil;


    @PostMapping("/oauth/kakao")
    public LoginResponse oauthLoginKakao(@RequestBody @Valid KakaoLoginRequest kakaoLoginRequest) {
        validationUtil.kakaoLoginValidation(kakaoLoginRequest.getOauthId(), kakaoLoginRequest.getAccessToken());
        Long avatarId = accountService.oauthLogin(kakaoLoginRequest.toLoginDto());
        return sessionTokenService.createToken(kakaoLoginRequest.toTokenDto(avatarId));
    }

    @PostMapping("/tokenlogin")
    public LoginResponse tokenLogin(@RequestBody @Valid TokenLoginRequest tokenLoginRequest) {
        return sessionTokenService.tokenLogin(tokenLoginRequest);
    }


    @PostMapping("/signup/kakao")
    public LoginResponse signUpKakao(@RequestBody @Valid KakaoSignUpRequest kakaoSignUpRequest) {
        validationUtil.kakaoLoginValidation(kakaoSignUpRequest.getOauthId(), kakaoSignUpRequest.getRefreshToken());

        Long avatarId = accountService.signUp(kakaoSignUpRequest.toSignUpDto(), Platform.KAKAO);

        return sessionTokenService.createToken(new CreateTokenDto(
                kakaoSignUpRequest.getDeviceId(),
                kakaoSignUpRequest.getDeviceType(),
                avatarId));
    }

    @PostMapping("/signup/apple")
    public LoginResponse signUpApple(@RequestBody @Valid AppleSignUpRequest appleSignUpRequest) {
        return null;
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "token") String value) {
        sessionTokenService.deActiveToken(value);
    }

    @PostMapping("/activate")
    public LoginResponse activate(@RequestBody @Valid KakaoLoginRequest kakaoLoginRequest) {
        accountService.activate(kakaoLoginRequest.getOauthId());
        Long avatarId = accountService.oauthLogin(kakaoLoginRequest.toLoginDto());
        return sessionTokenService.createToken(kakaoLoginRequest.toTokenDto(avatarId));
    }

    @GetMapping("/leave")
    public void leave(@LoginAvatarId Long avatarId, HttpSession session) {
        accountService.leave(avatarId);
        session.invalidate();
    }

}

package com.cherish.backend.controller;

import com.cherish.backend.controller.argumentresolver.LoginAvatarId;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.service.AccountService;
import com.cherish.backend.service.SessionTokenService;
import com.cherish.backend.service.dto.CreateTokenDto;
import com.cherish.backend.util.SocialLoginValidationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SessionTokenService sessionTokenService;
    private final SocialLoginValidationUtil validationUtil;


    @PostMapping("/oauthlogin")
    public LoginResponse oauthLogin(@RequestBody LoginRequest loginRequest) {
        validationUtil.validation(loginRequest.getOauthId(), loginRequest.getAccessToken(), loginRequest.getPlatform());
        Long avatarId = accountService.oauthLogin(loginRequest);
        return sessionTokenService.createToken(loginRequest.toTokenDto(avatarId));
    }

    @PostMapping("/tokenlogin")
    public LoginResponse tokenLogin(@RequestBody TokenLoginRequest tokenLoginRequest) {
        return sessionTokenService.tokenLogin(tokenLoginRequest);
    }

    @PostMapping("/activate")
    public LoginResponse activate(@RequestBody LoginRequest loginRequest) {
        accountService.activate(loginRequest.getOauthId());
        Long avatarId = accountService.oauthLogin(loginRequest);
        return sessionTokenService.createToken(loginRequest.toTokenDto(avatarId));
    }

    @PostMapping("/signup")
    public LoginResponse signUp(@RequestBody SignUpRequest signUpRequest) {
        validationUtil.validation(signUpRequest.getOauthId(), signUpRequest.getAccessToken(), signUpRequest.getPlatform());

        Long avatarId = accountService.signUp(signUpRequest);

        return sessionTokenService.createToken(new CreateTokenDto(
                signUpRequest.getDeviceId(),
                signUpRequest.getDeviceType(),
                avatarId));
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "token") String value) {
        sessionTokenService.deActiveToken(value);
    }

    @GetMapping("/leave")
    public void leave(@LoginAvatarId Long avatarId, HttpSession session) {
        accountService.leave(avatarId);
        session.invalidate();
    }

}

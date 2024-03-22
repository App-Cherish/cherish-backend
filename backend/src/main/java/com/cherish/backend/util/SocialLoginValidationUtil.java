package com.cherish.backend.util;

import com.cherish.backend.domain.Platform;
import com.cherish.backend.exception.SocialLoginValidationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialLoginValidationUtil {

    private final RestTemplate restTemplate;

    public void kakaoLoginValidation(String code, String accessToken) {
        if (!code.equals(kakaoLoginRequest(accessToken))) {
            throw new SocialLoginValidationException();
        }
    }

    private String kakaoLoginRequest(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        ResponseEntity<KakaoValidationResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity(headers), KakaoValidationResponseDTO.class);

        return response.getBody().getId();
    }

    private void appleLoginValidation(String code,String accessToken) {

    }

}

@Getter
@NoArgsConstructor
class KakaoValidationResponseDTO {
    private String id;
    private String expires_in;
    private String app_id;
    private String appId;
    private String expiresInMillis;
}

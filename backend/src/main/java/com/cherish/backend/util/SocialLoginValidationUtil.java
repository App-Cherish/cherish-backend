package com.cherish.backend.util;

import com.cherish.backend.domain.Platform;
import com.cherish.backend.exception.WrongOauthIdException;
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

    public void validation(String code, String accessToken, String platform) {
        if (platform.equals(Platform.KAKAO.getValue())) {
            kakaoLoginValidation(code, kakaoLoginRequest(accessToken));
        }
        if (platform.equals(Platform.APPLE.getValue())) {
            appleLoginValidation(code, accessToken);
        }
    }

    private void kakaoLoginValidation(String code, String validationCode) {
        if (!code.equals(validationCode)) {
            throw new WrongOauthIdException();
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


    class KakaoValidationResponseDTO {
        private String id;
        private String expires_in;
        private String app_id;

        public String getId() {
            return id;
        }
    }

}

package com.cherish.backend.config;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        if(response.getStatusCode().is4xxClientError()) {
            throw new ClientRequestException();
        }

        if (response.getStatusCode().is5xxServerError()){
            throw new ServerRequestException();
        }

    }

    class ClientRequestException extends RuntimeException {
        public ClientRequestException() {
            super("클라이언트 요청 값이 잘못 되었습니다.");
        }
    }

    class ServerRequestException extends RuntimeException {
        public ServerRequestException() {super("서버 내부 오류입니다.");}
    }
}

package com.cherish.backend.infra.logging;

import com.cherish.backend.controller.ConstValue;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private static final String HTTP_REQUEST_FORMAT = "|HTTP REQUEST| REQUEST ID : {} | Method: {} URI: {} ";
    private static final String SESSION_VALUE = "| session ID : {} |";
    private static final String REQUEST_BODY_FORMAT = "| Content-Type: {} \n Body: {}\n";
    private static final String HTTP_RESPONSE_FORMAT = "|HTTP RESPONSE | REQUEST ID : {} StatusCode: {}";
    private static final String HTTP_RESPONSE_WITH_BODY_FORMAT = HTTP_RESPONSE_FORMAT + "\nBody: {}";
    private static final String QUERY_COUNTER_FORMAT = "|REQUEST PROCESSED  | REQUEST ID : {} | METHOD : {} | URL : {} | [TIME: {} ms] [QUERIES: {}]";
    private static final String QUERY_STRING_PREFIX = "?";

    private final QueryCountInspector queryCountInspector;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();
        MDC.put("traceId", UUID.randomUUID().toString());
        queryCountInspector.startCounter();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        logProcessedRequest(wrappedRequest, wrappedResponse, System.currentTimeMillis() - startTime);
        queryCountInspector.clearCounter();
        MDC.clear();
        wrappedResponse.copyBodyToResponse();
    }

    private void logProcessedRequest(ContentCachingRequestWrapper request,
                                     ContentCachingResponseWrapper response,
                                     long duration) {

        String uuid = UUID.randomUUID().toString();
        logRequest(request, uuid);
        logResponse(response, uuid);
        logQueryCount(duration, request.getMethod(), request.getRequestURI(), uuid);
    }

    private void logRequest(ContentCachingRequestWrapper request, String uuid) {
        String requestBody = new String(request.getContentAsByteArray());


        if (request.getSession().getAttribute(ConstValue.sessionName) == null) {
            log.info(HTTP_REQUEST_FORMAT + REQUEST_BODY_FORMAT,
                    uuid,
                    getRequestUri(request),
                    request.getMethod(),
                    request.getContentType(),
                    requestBody);
            return;
        }

        log.info(HTTP_REQUEST_FORMAT + SESSION_VALUE + REQUEST_BODY_FORMAT,
                uuid,
                getRequestUri(request),
                request.getMethod(),
                request.getSession().getAttribute(ConstValue.sessionName),
                request.getContentType(),
                requestBody);
    }

    private void logResponse(final ContentCachingResponseWrapper response, String uuid) {
        final Optional<String> body = getJsonResponseBody(response);

        if (body.isPresent()) {
            log.info(HTTP_RESPONSE_WITH_BODY_FORMAT, uuid, response.getStatus(), body.get());
            return;
        }

        log.info(HTTP_RESPONSE_FORMAT, uuid, response.getStatus());
    }

    private void logQueryCount(long duration, String method, String uri, String uuid) {
        Long queryCount = queryCountInspector.getQueryCount();

        if (queryCount >= 10) {
            log.warn(QUERY_COUNTER_FORMAT, uuid, method, uri, duration, queryCount);
            return;
        }

        log.info(QUERY_COUNTER_FORMAT, uuid, method, uri, duration, queryCount);
    }

    private String getRequestUri(ContentCachingRequestWrapper request) {
        final String requestURI = request.getRequestURI();
        final String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURI;
        }

        return requestURI + QUERY_STRING_PREFIX + queryString;
    }

    private Optional<String> getJsonResponseBody(final ContentCachingResponseWrapper response) {
        if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return Optional.of(new String(response.getContentAsByteArray()));
        }

        return Optional.empty();
    }

}

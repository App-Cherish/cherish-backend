package com.cherish.backend.filter;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.util.QueryCountInspector;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {


    private final QueryCountInspector queryCountInspector;


    private static void logRequest(RequestWrapper request, Long time) throws IOException {
        String queryString = request.getQueryString();
        log.info("Request : {} uri=[{}] content-type=[{}] time : [{}ms]", request.getMethod(), queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString, request.getContentType(), time);
        logPayload("Request", request.getContentType(), request.getInputStream());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayload("Response", response.getContentType(), response.getContentInputStream());
    }

    private static void logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                String contentString = new String(content);
                log.info("{} Payload: {}", prefix, contentString);
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(MediaType.valueOf("text/*"), MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.valueOf("application/*+json"), MediaType.valueOf("application/*+xml"), MediaType.MULTIPART_FORM_DATA);
        return VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    private void logQueryCount(RequestWrapper request, Long duration) {

        if (queryCountInspector.getQueryCount() == null) {
            return;
        }

        Long queryCount = queryCountInspector.getQueryCount();


        if (queryCount >= 10) {
            log.warn("METHOD : {} | URL : {} | [TIME: {} ms] [QUERIES: {}]", request.getMethod(), request.getRequestURI(), duration, queryCount);
            return;
        }

        log.info("METHOD : {} | URL : {} | [TIME: {} ms] [QUERIES: {}]", request.getMethod(), request.getRequestURI(), duration, queryCount);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        queryCountInspector.startCounter();
        MDC.put("traceId", UUID.randomUUID().toString());

        if (request.getSession().getAttribute(ConstValue.sessionName) != null) {
            MDC.put("sessionId", String.valueOf(request.getSession().getAttribute(ConstValue.sessionName)));
        } else {
            MDC.put("sessionId", "Customer");
        }
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain, System.currentTimeMillis() - startTime);
            queryCountInspector.clearCounter();
            MDC.clear();
        }
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain, Long time) throws ServletException, IOException {
        try {
            logRequest(request, time);
            filterChain.doFilter(request, response);
        } finally {
            logQueryCount(request, time);
            logResponse(response);
            response.copyBodyToResponse();
        }
    }


}

class ResponseWrapper extends ContentCachingResponseWrapper {
    public ResponseWrapper(HttpServletResponse response) {
        super(response);

    }
}

class RequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedInputStream;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedInputStream = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private InputStream cachedBodyInputStream = new ByteArrayInputStream(cachedInputStream);

            @Override
            public boolean isFinished() {
                try {
                    return cachedBodyInputStream.available() == 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() throws IOException {
                return cachedBodyInputStream.read();
            }
        };
    }
}


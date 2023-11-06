package com.cherish.backend.infra.interceptor;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.exception.NotFoundSessionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if(session == null || session.getAttribute(ConstValue.sessionName )==null) {
            throw new NotFoundSessionException();
        }

        return true;
    }
}

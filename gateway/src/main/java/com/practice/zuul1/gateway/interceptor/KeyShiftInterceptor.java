package com.practice.zuul1.gateway.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Luo Bao Ding
 * @since 2018/6/16
 */
public class KeyShiftInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String method = request.getMethod();
            if ("get".equalsIgnoreCase(method)) {
                return true;
            } else {
                shiftKey(request, "sign");//todo can not acquire post key
                shiftKey(request, "uid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void shiftKey(HttpServletRequest request, String key) {
        String val = request.getHeader(key);
        if (StringUtils.isBlank(val)) {
            val = request.getParameter(key);
            request.setAttribute(key, val);
        }

    }
}

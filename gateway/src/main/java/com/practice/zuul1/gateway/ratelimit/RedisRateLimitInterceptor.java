package com.practice.zuul1.gateway.ratelimit;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Luo Bao Ding
 * @since 2018/7/5
 */
public class RedisRateLimitInterceptor extends HandlerInterceptorAdapter {
    private RedisRateLimiter redisRateLimiter;

    public RedisRateLimitInterceptor(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String remoteAddress = request.getRemoteAddr();
        boolean allowed = redisRateLimiter.isAllowed(remoteAddress);
        if (allowed) {
            return true;
        } else {
            response.setContentType("text/plain");
            response.setStatus(429);
            response.getWriter().append("too many request");
            return false;
        }
    }
}

package com.practice.zuul1.gateway.config;

import com.practice.zuul1.gateway.interceptor.KeyShiftInterceptor;
import com.practice.zuul1.gateway.ratelimit.RedisRateLimitInterceptor;
import com.practice.zuul1.gateway.ratelimit.RedisRateLimiter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Luo Bao Ding
 * @since 2018/6/16
 */
public class ZuulInterceptorConfig {

    @Bean
    public KeyShiftInterceptor keyShiftInterceptor() {
        return new KeyShiftInterceptor();
    }

    @Bean
    public RedisRateLimitInterceptor redisRateLimitInterceptor(RedisRateLimiter redisRateLimiter) {
        return new RedisRateLimitInterceptor(redisRateLimiter);
    }

    @Bean
    @ConfigurationProperties("redis-rate-limiter")
    public RedisRateLimiter redisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new RedisRateLimiter(stringRedisTemplate);
    }

    @Bean
    public ZuulInterceptorInjector zuulInterceptorInjector(KeyShiftInterceptor keyShiftInterceptor, RedisRateLimitInterceptor redisRateLimitInterceptor) {
        return new ZuulInterceptorInjector(keyShiftInterceptor, redisRateLimitInterceptor);
    }

    private static class ZuulInterceptorInjector extends InstantiationAwareBeanPostProcessorAdapter {
        private final KeyShiftInterceptor keyShiftInterceptor;

        private final RedisRateLimitInterceptor redisRateLimitInterceptor;

        public ZuulInterceptorInjector(KeyShiftInterceptor keyShiftInterceptor, RedisRateLimitInterceptor redisRateLimitInterceptor) {
            this.keyShiftInterceptor = keyShiftInterceptor;
            this.redisRateLimitInterceptor = redisRateLimitInterceptor;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (bean instanceof ZuulHandlerMapping) {
                ZuulHandlerMapping zuulHandlerMapping = (ZuulHandlerMapping) bean;
                zuulHandlerMapping.setInterceptors(keyShiftInterceptor, redisRateLimitInterceptor);
            }
            return super.postProcessAfterInstantiation(bean, beanName);
        }
    }


}

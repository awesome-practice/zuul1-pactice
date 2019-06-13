package com.practice.zuul1.gateway.config;

import com.practice.zuul1.gateway.fallback.MyFallbackProvider;
import org.springframework.context.annotation.Bean;

/**
 * @author Luo Bao Ding
 * @since 2018/12/6
 */
public class FallbackConfig {
    @Bean
    public MyFallbackProvider myFallbackProvider() {
        return new MyFallbackProvider();
    }
}

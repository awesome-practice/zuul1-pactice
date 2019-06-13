package com.practice.zuul1.gateway.config;

import com.practice.zuul1.gateway.filters.servlet.ExtractKeyServletFilter;
import org.springframework.context.annotation.Bean;

/**
 * @author Luo Bao Ding
 * @since 2018/12/12
 */
public class ServletFilterConfig {

    @Bean
    public ExtractKeyServletFilter extractKeyServletFilter() {
        return new ExtractKeyServletFilter();
    }
}

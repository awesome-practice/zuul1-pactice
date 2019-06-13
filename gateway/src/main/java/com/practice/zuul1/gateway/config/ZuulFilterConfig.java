package com.practice.zuul1.gateway.config;

import com.practice.zuul1.gateway.filters.zuul.pre.ExtractKeyZuulPreFilter;
import com.practice.zuul1.gateway.filters.zuul.pre.ParsePostBodyKeyZuulFilter;
import org.springframework.context.annotation.Bean;

/**
 * @author Luo Bao Ding
 * @since 2018/12/6
 */
public class ZuulFilterConfig {

    //    @Bean
    public ParsePostBodyKeyZuulFilter simpleZuulFilter() {
        return new ParsePostBodyKeyZuulFilter();
    }

    @Bean
    public ExtractKeyZuulPreFilter extractKeyZuulPreFilter() {
        return new ExtractKeyZuulPreFilter();
    }

}

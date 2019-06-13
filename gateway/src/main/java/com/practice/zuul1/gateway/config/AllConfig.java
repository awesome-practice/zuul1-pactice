package com.practice.zuul1.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Luo Bao Ding
 * @since 2018/12/6
 */
@Configuration
@Import({
        FallbackConfig.class
        , ZuulFilterConfig.class
//        , ZuulInterceptorConfig.class
//        , MetricConfig.class
//        , OptimizeConfig.class
//        , ServletFilterConfig.class
})
public class AllConfig {
}

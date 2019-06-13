package com.practice.zuul1.gateway.config;

import com.practice.zuul1.gateway.metrics.SemaphoreMetric;
import org.springframework.context.annotation.Bean;

/**
 * @author Luo Bao Ding
 * @since 2018/12/8
 */
public class MetricConfig {

    @Bean
    public SemaphoreMetric semaphoreMetric() {
        return new SemaphoreMetric();
    }
}

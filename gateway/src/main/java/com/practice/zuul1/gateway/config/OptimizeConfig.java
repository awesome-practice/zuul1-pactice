package com.practice.zuul1.gateway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * @author Luo Bao Ding
 * @since 2018/12/12
 */
public class OptimizeConfig {

    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilterFilterRegistrationBean(
            HiddenHttpMethodFilter hiddenHttpMethodFilter) {
        FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean = new FilterRegistrationBean<>(hiddenHttpMethodFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

}

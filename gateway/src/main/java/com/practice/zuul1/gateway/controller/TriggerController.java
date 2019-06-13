package com.practice.zuul1.gateway.controller;

import com.practice.zuul1.gateway.metrics.SemaphoreMetric;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Luo Bao Ding
 * @since 2018/12/8
 */
//@RestController
@RequestMapping("/trigger")
public class TriggerController {

    private final SemaphoreMetric semaphoreMetric;

    public TriggerController(SemaphoreMetric semaphoreMetric) {
        this.semaphoreMetric = semaphoreMetric;
    }

    @RequestMapping("/semaphoreMetric")
    public String semaphoreMetric() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        semaphoreMetric.logSemaphore();
        return "trigger success";
    }


}

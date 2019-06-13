package com.practice.zuul1.server.book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * @author Luo Bao Ding
 * @since 2018/12/5
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final int port;

    public TestController(@Value("${server.port}") int port) {
        this.port = port;
    }

    @RequestMapping("/sleep/{timeInSeconds}")
    public String sleep(@PathVariable("timeInSeconds") int timeInSeconds) throws InterruptedException {
        System.out.println(port + ", " + Instant.now());
        Thread.sleep(timeInSeconds * 1000);
        return "success";
    }
}

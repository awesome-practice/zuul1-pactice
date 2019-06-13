package com.practice.zuul1.server.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class BookApplication {

    private final Environment env;

    public BookApplication(Environment env) {
        this.env = env;
    }

    @RequestMapping(value = "/available")
    public String available() {
        String msg = env.getProperty("server.port") + ": Spring in Action";
        System.out.println("msg = " + msg);
        return msg;
    }

    @RequestMapping(value = "/checked-out")
    public String checkedOut() {
        String msg = env.getProperty("server.port") + ": Spring Boot in Action";
        System.out.println("msg = " + msg);
        return msg;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
    }
}
package com.practice.zuul1.gateway.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    @PostMapping("/urlencoded")
    public Object urlencoded(String sign, String uid) {
        String echo = "sign = [" + sign + "], uid = [" + uid + "]";
        System.out.println(echo);
        return echo;
    }

    @PostMapping("/multipart")
    public String multipart(String sign, String uid) {
        String echo = "sign = [" + sign + "], uid = [" + uid + "]";
        System.out.println(echo);
        return "redirect: http://httbin.org/post";//todo  没生效
    }


}

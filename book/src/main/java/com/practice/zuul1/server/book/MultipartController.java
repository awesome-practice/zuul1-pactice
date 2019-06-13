package com.practice.zuul1.server.book;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Luo Bao Ding
 * @since 2018/12/12
 */
@Controller
public class MultipartController {

    @ResponseBody
    @PostMapping("/form")
    public String handleFormUpload(@RequestParam("name") String name,
                                   @RequestParam("file") MultipartFile file) throws IOException {

        Assert.isTrue(!file.isEmpty(), "the file should not be empty");
        byte[] bytes = file.getBytes();
        String fileContent = "file = " + new String(bytes, StandardCharsets.UTF_8);
        System.out.println("name = [" + name + "], fileContent = [" + fileContent + "]");
        return fileContent;
    }


}

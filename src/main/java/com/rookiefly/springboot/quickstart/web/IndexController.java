package com.rookiefly.springboot.quickstart.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rookiefly on 2017/7/13.
 */
@RestController
public class IndexController {

    @RequestMapping("/")
    public String sayHello() {
        return "Hello World!";
    }

    @RequestMapping("/status")
    public String status() {
        return "OK";
    }
}

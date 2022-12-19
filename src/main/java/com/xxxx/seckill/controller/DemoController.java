package com.xxxx.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;

/**
 * Author: asus
 * Date: 2022/10/22 13:37
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "你好！");
        return "hello";
    }

}

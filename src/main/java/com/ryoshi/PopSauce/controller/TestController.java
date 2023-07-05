package com.ryoshi.PopSauce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/text")
    public String getText(Model model){
        double d = Math.random();
        return String.valueOf(d);
    }

}
package com.example.wiki.controller;

import com.example.wiki.domain.Demo;
import com.example.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class DemoController {
    @Resource
    private DemoService demoService;

    @GetMapping("/demo")
    public List<Demo> list(){
        return demoService.list();
    }
}

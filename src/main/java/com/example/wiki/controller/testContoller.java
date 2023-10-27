package com.example.wiki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//这呢就是接口层 写一个最简单的接口
@RestController //返回字符串 相当于@Controller+@ResponseBody 更多的是返回json对象
//@Controller   //返回页面  现在前后端分离不需要返回页面了
public class testContoller {

//    权限 返回类型 方法名（参数）
//    将方法变成接口 添加请求地址
//    常见的请求方式增删改查 GET POST PUT DELETE
//    RESTFUL风格：正常user?id=1   -->    user/1
//    @RequestMapping支持所有请求方式 可以指定请求方法 (value="/hello", method=RequestMethod.GET)
//    @PostMapping
//    @GetMapping
//    @PutMapping
//    @DeleteMapping
//    返回码405 接口不支持这个请求方法
//    404 请求访问不到 没有这样一个接口
    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }

}

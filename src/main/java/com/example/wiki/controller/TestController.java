package com.example.wiki.controller;

import com.example.wiki.domain.Test;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

//这呢就是接口层 写一个最简单的接口
@RestController //返回字符串 相当于@Controller+@ResponseBody 更多的是返回json对象
//@Controller   //返回页面  现在前后端分离不需要返回页面了
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;

//    注入属性，获取位于application.properties中配置的属性 @Value("${test.hello:TEST}")添加默认配置
//    @Value("${test.hello}")
    private String testHello;

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
//        return "hello world!!" + testHello;
        return "hello world!!";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
//        return "hello world!!" + testHello;
        return "hello world!!" + name;
    }

    @GetMapping("/test")
    public List<Test> list(){
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }


}

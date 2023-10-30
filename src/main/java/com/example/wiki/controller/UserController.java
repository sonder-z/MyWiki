package com.example.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wiki.req.UserLoginReq;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserResetPasswordReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.UserLoginResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.UserService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;


    /*
        封装请求参数
     */
    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    //用json方式提交需要用@RequestBody接收
    public CommonResp save(@RequestBody @Valid UserSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        //没有返回值，不传泛型，CommonResp返回默认值
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    //PathVariable:从url接收参数
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    //PathVariable:从url接收参数
    public CommonResp resetPassword(@RequestBody @Valid UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody @Valid UserLoginReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        //登录之后返回用户信息
        UserLoginResp userLoginResp = userService.login(req);
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis", token);

        //以token作为key，以userLoginResp作为value，时效，时效单位
        //要把token返回给前端，所以返回参数里面添加token属性
        userLoginResp.setToken(token.toString());
        //远程存储需要将对象序列化，获取的时候在反序列化
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp),3600*24, TimeUnit.SECONDS);

        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token：{}", token);
        return resp;
    }



}

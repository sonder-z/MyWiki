package com.example.wiki.controller;

import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

//    @GetMapping("/list")
//    public List<User> list(){
//        return userService.list();
//    }


    /*
        模糊查询
     */
//    @GetMapping("/filter")
//    public CommonResp filter(String name){
//        CommonResp<List<User>> resp = new CommonResp<>();
//        List<User> list = userService.filter(name);
//        resp.setContent(list);
//        return resp;
//    }

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



}

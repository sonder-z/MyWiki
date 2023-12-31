package com.example.wiki.controller;

import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {
    @Resource
    private DocService docService;

    /*
        封装请求参数
     */
    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    //用json方式提交需要用@RequestBody接收
    public CommonResp save(@RequestBody @Valid DocSaveReq req){
        //没有返回值，不传泛型，CommonResp返回默认值
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    //PathVariable:从url接收参数
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        docService.delete(id);
        return resp;
    }


    @DeleteMapping("/delete/{idsStr}")   //将请求参数设置为一个数组字符串，例如：["1,2,3"]
    public CommonResp delete(@PathVariable String idsStr){  //自动映射
        CommonResp resp = new CommonResp<>();
        //将数组字符串转化成数组
        List<String> list = Arrays.asList(idsStr.split(","));
        //删除一个数组（列表）
        docService.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        docService.vote(id);
        return resp;
    }





}

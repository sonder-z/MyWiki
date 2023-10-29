package com.example.wiki.controller;

import com.example.wiki.domain.Ebook;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.EbookResp;
import com.example.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Resource
    private EbookService ebookService;

//    @GetMapping("/list")
//    public List<Ebook> list(){
//        return ebookService.list();
//    }


    /*
        模糊查询
     */
//    @GetMapping("/filter")
//    public CommonResp filter(String name){
//        CommonResp<List<Ebook>> resp = new CommonResp<>();
//        List<Ebook> list = ebookService.filter(name);
//        resp.setContent(list);
//        return resp;
//    }

    /*
        封装请求参数
     */
    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<List<EbookResp>> resp = new CommonResp<>();
        List<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

}

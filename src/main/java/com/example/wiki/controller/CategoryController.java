package com.example.wiki.controller;

import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryQueryResp;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /* 查询 */
    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    /* 不分页查询 */
    @GetMapping("/all")
    public CommonResp all(@Valid CategoryQueryReq req) {
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> categoryRespPageResp = categoryService.all(req);
        resp.setContent(categoryRespPageResp);
        return resp;
    }

    /* 保存 */
    @PostMapping("/save")
    public CommonResp save(@RequestBody @Valid CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }


    /* 删除 */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }


}

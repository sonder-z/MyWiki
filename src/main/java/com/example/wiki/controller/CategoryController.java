package com.example.wiki.controller;

import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryResp;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /* 查询 */
    @GetMapping("/list")
    public CommonResp list(CategoryQueryReq req) {
        CommonResp<PageResp<CategoryResp>> resp = new CommonResp<>();
        PageResp<CategoryResp> categoryRespPageResp = categoryService.list(req);
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

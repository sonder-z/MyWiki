package com.example.wiki.service;

import com.example.wiki.domain.Category;
import com.example.wiki.domain.CategoryExample;
import com.example.wiki.mapper.CategoryMapper;
import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SnowFlake snowFlake;


    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> list = categoryMapper.selectByExample(null);
        List<CategoryQueryResp> categorys = CopyUtil.copyList(list, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> resp = new PageResp<>();
        resp.setList(categorys);
        PageInfo<Category> pageInfo = new PageInfo<>(list);
        //总条数和总页数
        pageInfo.getTotal();
        pageInfo.getPages();
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        resp.setTotal(pageInfo.getTotal());
        return resp;
    }

    public List<CategoryQueryResp> all(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> list = categoryMapper.selectByExample(categoryExample);
        List<CategoryQueryResp> resp = CopyUtil.copyList(list, CategoryQueryResp.class);

        return resp;
    }

    public void save (CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateByPrimaryKeySelective(category);
        }

    }

    public void delete (Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }


}

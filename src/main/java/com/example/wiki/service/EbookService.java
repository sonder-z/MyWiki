package com.example.wiki.service;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<Ebook> list(){
        return ebookMapper.selectByExample(null);
    }


//    添加一个模糊查询服务
    public List<Ebook> filter(String name){
//        创建一个Example
        EbookExample ebookExample = new EbookExample();
//        相当于创建一个where条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
//        设置where条件--针对name字段的like模糊查询
        criteria.andNameLike("%" + name + "%");
//        使用example
        return ebookMapper.selectByExample(ebookExample);
    }
}

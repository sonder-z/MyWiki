package com.example.wiki.service;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<Ebook> list(){
        return ebookMapper.selectByExample(null);
    }


////    添加一个模糊查询服务
//    public List<Ebook> filter(String name){
////        创建一个Example
//        EbookExample ebookExample = new EbookExample();
////        相当于创建一个where条件
//        EbookExample.Criteria criteria = ebookExample.createCriteria();
////        设置where条件--针对name字段的like模糊查询
//        criteria.andNameLike("%" + name + "%");
////        使用example
//        return ebookMapper.selectByExample(ebookExample);
//    }
//}

    //    添加一个模糊查询服务,封装请求参数和返回实体
    public List<EbookResp> filter(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebooks = ebookMapper.selectByExample(ebookExample);

//        需要把查出来的列表copy到另外一个列表里
        List<EbookResp> respList = new ArrayList<>();
//        循环查出来的ebooks列表
        for (Ebook ebook : ebooks) {
            EbookResp ebookResp = new EbookResp();
//            分别将列表里的字段set到eBookResp中，可以使用工具类从一个实体拷贝到另一个实体
//            ebookResp.setId(ebook.getId());
            BeanUtils.copyProperties(ebook,ebookResp);
//            将获取到值的resp对象放到列表中
            respList.add(ebookResp);
        }

        return respList;

    }
}
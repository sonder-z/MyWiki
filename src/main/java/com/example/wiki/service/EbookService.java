package com.example.wiki.service;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookQueryReq;
import com.example.wiki.req.EbookSaveReq;
import com.example.wiki.resp.EbookQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

//    public List<Ebook> list(){
//        return ebookMapper.selectByExample(null);
//    }


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
    public PageResp<EbookQueryResp> list(EbookQueryReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        // 添加一个判断实现动态sql
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //页码和每页条数,分页只对下面第一条查询语句起作用，所以最好挨着查询的代码
        //封装分页的请求参数后 可以通过前端传入请求参数动态分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(ebookExample);

        //提供了pageinfo类，把列表类传进来就可以查看他的分页信息
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        //总条数和总页数
        pageInfo.getTotal();
        pageInfo.getPages();
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

//        需要把查出来的列表copy到另外一个列表里
//        List<EbookResp> respList = new ArrayList<>();
////        循环查出来的ebooks列表
//        for (Ebook ebook : ebooks) {
//            EbookResp ebookResp = new EbookResp();
////            分别将列表里的字段set到eBookResp中，可以使用工具类从一个实体拷贝到另一个实体
////            ebookResp.setId(ebook.getId());
//            BeanUtils.copyProperties(ebook,ebookResp);
////            将获取到值的resp对象放到列表中
//            respList.add(ebookResp);
//        }

        List<EbookQueryResp> respList = CopyUtil.copyList(ebooks, EbookQueryResp.class);
        //new一个分页的返回类
        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);

        return pageResp;

    }

    public void save(EbookSaveReq req) {
        //copy一份请求实体作为实体类
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        //保存分为新增保存和更新保存,通过id进行判断
        if (ObjectUtils.isEmpty(req.getId())) {
            //id不存在，新增
            ebookMapper.insert(ebook);  //不加Selective不传参数的值会被null覆盖
//            ebookMapper.insertSelective(ebook);
        } else {
            //id存在，更新
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }
}
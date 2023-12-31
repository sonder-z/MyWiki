package com.example.wiki.service;

import com.example.wiki.domain.Content;
import com.example.wiki.domain.Doc;
import com.example.wiki.domain.DocExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
import com.example.wiki.mapper.ContentMapper;
import com.example.wiki.mapper.DocMapper;
import com.example.wiki.mapper.DocMapperCust;
import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.RedisUtil;
import com.example.wiki.util.RequestContext;
import com.example.wiki.util.SnowFlake;
import com.example.wiki.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    @Resource
    private DocMapper docMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WebSocketServer webSocketServer;

    @Resource
    private WsService wsService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    //    添加一个模糊查询服务,封装请求参数和返回实体
    public PageResp<DocQueryResp> list(DocQueryReq req){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();

        //页码和每页条数,分页只对下面第一条查询语句起作用，所以最好挨着查询的代码
        //封装分页的请求参数后 可以通过前端传入请求参数动态分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docs = docMapper.selectByExample(docExample);

        //提供了pageinfo类，把列表类传进来就可以查看他的分页信息
        PageInfo<Doc> pageInfo = new PageInfo<>(docs);
        //总条数和总页数
        pageInfo.getTotal();
        pageInfo.getPages();
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DocQueryResp> respList = CopyUtil.copyList(docs, DocQueryResp.class);
        //new一个分页的返回类
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);

        return pageResp;

    }

    /*
        插入或更新
     */
    @Transactional
    public void save(DocSaveReq req) {
        //copy一份请求实体作为实体类
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);  //copy一份content
        //保存分为新增保存和更新保存,通过id进行判断
        if (ObjectUtils.isEmpty(req.getId())) {
            //id不存在，新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);  //不加Selective不传参数的值会被null覆盖
            content.setId(doc.getId());   //content的id与文档id相同直接获取文档id即可
            contentMapper.insert(content);
//            docMapper.insertSelective(doc);
        } else {
            //id存在，更新
            docMapper.updateByPrimaryKey(doc);
            //contentMapper.updateByPrimaryKeyWithBLOBs(content);  //带大字段的更新
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {  //确保doc_id存在但content_id不存在的时候也可以更新成功
                contentMapper.insert(content);
            }
        }

    }

    /*
        删除
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        //条件是id在ids中
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        //44.516 WARN  [java.lang.NullPointerException] 空指针异常

        //阅读数加一
        docMapperCust.IncreaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";        //空值处理
        } else {
            return content.getContent();
        }
    }

    public void vote (Long id) {
//        docMapperCust.IncreaseVoteCount(id);
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)){
            docMapperCust.IncreaseVoteCount(id);

            //点赞成功推送消息
            Doc doc = docMapper.selectByPrimaryKey(id);
//            wsService.sendInfo("【"+doc.getName()+"】被点赞");
//            webSocketServer.sendInfo("【"+doc.getName()+"】被点赞");
            rocketMQTemplate.convertAndSend("VOTE_TOPIC","【" + doc.getName() + "】被点赞！");
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
    }

    public void updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    }

}
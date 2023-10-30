package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.UserQueryResp;
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
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    //    添加一个模糊查询服务,封装请求参数和返回实体
    public PageResp<UserQueryResp> list(UserQueryReq req){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        // 添加一个判断实现动态sql
        if (!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andNameLike("%" + req.getLoginName() + "%");
        }
        //页码和每页条数,分页只对下面第一条查询语句起作用，所以最好挨着查询的代码
        //封装分页的请求参数后 可以通过前端传入请求参数动态分页
        PageHelper.startPage(req.getPage(),req.getSize());
        List<User> users = userMapper.selectByExample(userExample);

        //提供了pageinfo类，把列表类传进来就可以查看他的分页信息
        PageInfo<User> pageInfo = new PageInfo<>(users);
        //总条数和总页数
        pageInfo.getTotal();
        pageInfo.getPages();
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<UserQueryResp> respList = CopyUtil.copyList(users, UserQueryResp.class);
        //new一个分页的返回类
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);

        return pageResp;

    }

    /*
        插入或更新
     */
    public void save(UserSaveReq req) {
        //copy一份请求实体作为实体类
        User user = CopyUtil.copy(req, User.class);
        //保存分为新增保存和更新保存,通过id进行判断
        if (ObjectUtils.isEmpty(req.getId())) {
            //id不存在，新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);  //不加Selective不传参数的值会被null覆盖
//            userMapper.insertSelective(user);
        } else {
            //id存在，更新
            userMapper.updateByPrimaryKey(user);
        }

    }

    /*
        删除
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

}
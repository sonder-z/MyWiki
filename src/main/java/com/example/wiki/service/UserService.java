package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserLoginReq;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserResetPasswordReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.UserLoginResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
        // id为空
        if (ObjectUtils.isEmpty(req.getId())) {
            //查库，看有没有该用户
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                //用户不存在，新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);  //不加Selective不传参数的值会被null覆盖
            } else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
//            userMapper.insertSelective(user);
        } else {
            //id存在，更新,登录名不能修改,也不能修改密码，独立开发重置密码的接口
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
//            userMapper.updateByPrimaryKey(user);
        }

    }

    /*
        删除
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    /*
        通过用户名查用户数据
     */
    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);

        List<User> list = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }

    }

    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

     public UserLoginResp login(UserLoginReq req) {
         User user = CopyUtil.copy(req, User.class);
         //查用户名
         User userDB = selectByLoginName(user.getLoginName());
         if (ObjectUtils.isEmpty(userDB)){
             //用户不存在\
             LOG.info("用户名不存在，{}", req.getLoginName());
             throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
         } else {
             if (user.getPassword().equals(userDB.getPassword())){
                 //登录成功
                 UserLoginResp userLoginResp = CopyUtil.copy(userDB, UserLoginResp.class);
                 return userLoginResp;
             } else {
                 //登录失败，抛出业务异常中断业务
                 LOG.info("密码输入错误，{}", req.getPassword());
                 throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
             }
         }

    }




}
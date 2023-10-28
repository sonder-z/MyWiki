package com.example.wiki.service;

import com.example.wiki.domain.Test;
import com.example.wiki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

//业务代码 逻辑层 服务层 @service注解将这个类交给spring管理
@Service
public class TestService {

    @Resource
//    把TestMapper注入进来，@Resource是JDK自带的，@Autowired是spring的
    private TestMapper testMapper;

    public List<Test> list(){
        return testMapper.list();
    }
}

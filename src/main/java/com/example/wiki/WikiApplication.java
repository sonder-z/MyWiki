package com.example.wiki;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
//@SpringBootApplication里面包含@ComponentScan，可以扫描包，但只能扫描当前包和子包
//如果将WikiApplication放到单独的包里 需要添加@ComponentScan("com.example")
public class WikiApplication {
    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

//    添加一行注释试试
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WikiApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址：\thttp://127.0.0.1:{}", env.getProperty("server.port"));

    }

}

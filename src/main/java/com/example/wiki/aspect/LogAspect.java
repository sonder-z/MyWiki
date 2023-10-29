package com.example.wiki.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


@Component
@Aspect
public class LogAspect {
    // 添加日志打印
    private static final Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    /*
        定义一个切点
        com.example.wiki.controller下面的所有的controller所有的参数
    */
    @Pointcut("execution(public * com.example.wiki.controller..*Controller.*(..))")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        //获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取方法这边的信息
        Signature signature = joinPoint.getSignature();
        String name = signature.getName();

        //打印请求信息
        LOG.info("------------logAspect开始------------");
        LOG.info("请求地址：{} {}",request.getRequestURL().toString(),request.getMethod());
        LOG.info("类名方法：{}.{}", signature.getDeclaringTypeName(), name);
        LOG.info("远程地址：{}", request.getRemoteAddr());

        //打印请求参数，请求参数是从连接点获取的
        Object[] args  = joinPoint.getArgs();
//        LOG.info("请求参数: {}", JSONObject.toJSONString(args));

        //一堆请求参数需要进行一个循环，排除一些请求响应和大文件类型
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }

        String[] excludeProperties = {"password", "file"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter = filters.addFilter();
        excludeFilter.addExcludes(excludeProperties);
        LOG.info("请求参数: {}", JSONObject.toJSONString(args, excludeFilter));
    }

    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //获取目标方法的执行结果
        Object result = proceedingJoinPoint.proceed();
        //定义一个敏感字段的排除数组
        String[] excludeProperties = {"password", "file"};
        //创建一个属性过滤器
        PropertyPreFilters filters = new PropertyPreFilters();
        //创建一个排除属性过滤器
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter = filters.addFilter();
        // 指定排除属性过滤器：转换成JSON字符串时，排除哪些属性
        excludeFilter.addExcludes(excludeProperties);
        LOG.info("返回结果: {}", JSONObject.toJSONString(result, excludeFilter));
        LOG.info("------------- 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);

        return result;

    }
}

package com.example.wiki.controller;

import com.example.wiki.exception.BusinessException;
import com.example.wiki.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp validExceptionHandler(BindException e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOG.warn("参数校验失败：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        resp.setSuccess(false);
        resp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return resp;

    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp validExceptionHandler(BusinessException e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOG.warn("业务异常：{}", e.getCode().getDesc());
        resp.setSuccess(false);
        resp.setMessage(e.getCode().getDesc());
        return resp;

    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp validExceptionHandler(Exception e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOG.warn("系统异常：", e);
        resp.setSuccess(false);
        resp.setMessage("系统出现异常，请联系管理员");
        return resp;

    }





}

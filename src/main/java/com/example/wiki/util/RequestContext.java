package com.example.wiki.util;

import java.io.Serializable;

public class RequestContext implements Serializable {

    private static ThreadLocal<String> remoteAddr =  new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    //传进来一个远程IP set给我们定义的本地线程变量remoteAddr
    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }
}

package com.example.wiki.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/*
    查询出的实体封装成返回实体需要进行复制
 */
public class CopyUtil {
    // 单体复制
    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

//        创建目标对象
        T obj = null;
        try {
            obj = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        copy
        BeanUtils.copyProperties(source, obj);
        return obj;
    }


    //列表复制
    public static <T> List<T> copyList(List source, Class<T> clazz) {
//        创建一个新列表
        List<T> target = new ArrayList<>();
//        这个列表如果不是空的，循环复制
        if (!CollectionUtils.isEmpty(source)) {
            for (Object c : source) {
                T obj = copy(c, clazz);
                target.add(obj);
            }
        }

        return target;
    }


}

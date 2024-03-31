package com.example.laispring.component;

import com.example.laispring.annotation.ioc.Component;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 20:18
 * @Description: 简化版的spring切面类，简化了注解获取前置，返回通知
 */
@Component
public class MyAspect {
    public static void beforeInvoke(){
        System.out.println("前置通知");
    }
    public static void afterInvoke(){
        System.out.println("返回通知");
    }
}


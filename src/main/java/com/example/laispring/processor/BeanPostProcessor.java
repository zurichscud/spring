package com.example.laispring.processor;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 15:15
 * @Description: TODO
 */
public interface BeanPostProcessor {
    //初始化之前调用
    default Object postProcessBeforeInitialization(Object bean,String beanName){

        return bean;
    }
    //初始化方法之后调用
    default Object postProcessAfterInitialization(Object bean,String beanName){

        return bean;
    }
}

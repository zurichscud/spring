package com.example.laispring.component;

import com.example.laispring.annotation.ioc.Component;
import com.example.laispring.processor.BeanPostProcessor;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 15:20
 * @Description: 内置处理器，需要注入IOC容器
 */
@Component
public class LaiPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        System.out.println(beanName+" before init");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println(beanName+" after init");
        return bean;
    }
}

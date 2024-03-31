package com.example.laispring.component;

import com.example.laispring.annotation.ioc.Component;
import com.example.laispring.processor.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 20:45
 * @Description: Spring的AOP处理器
 */
@Component
public class AopPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        //实现AOP，得到bean的proxy
        if ("pig".equals(beanName)){
            return Proxy.newProxyInstance(AopPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //假设切入点为Runnable的run，原生Spring可以使用注解获取
                    Object result;
                    if ("run".equals(method.getName())){
                        MyAspect.beforeInvoke();
                        result=method.invoke(bean,args);
                        MyAspect.afterInvoke();
                    }else {
                        result=method.invoke(bean,args);
                    }
                    return result;
                }
            });
        }
        return bean;

    }
}

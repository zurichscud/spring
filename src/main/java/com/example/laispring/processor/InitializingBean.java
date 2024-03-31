package com.example.laispring.processor;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 14:58
 * @Description: bean的初始化方法
 */
public interface InitializingBean {
    /**
     * @Description: 在setter之后调用
     * @Param:
     * @Return:
     **/
    void afterPropertiesSet() throws Exception;
}

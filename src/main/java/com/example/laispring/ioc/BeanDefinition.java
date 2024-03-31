package com.example.laispring.ioc;

import lombok.Data;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 11:49
 * @Description: TODO
 */
@Data
public class BeanDefinition {
    private String scope;
    private Class<?> clazz;
}

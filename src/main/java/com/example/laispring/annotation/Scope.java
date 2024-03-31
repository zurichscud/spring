package com.example.laispring.annotation;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 12:14
 * @Description: TODO
 */
public @interface Scope {
    String value() default "singleton";
}

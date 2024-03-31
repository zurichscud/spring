package com.example.laispring.annotation;

import java.lang.annotation.*;

/**
 * @Author: zurichscud
 * @Date: 2024/3/30 11:12
 * @Description: TODO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";

}

package com.example.laispring.component;

import com.example.laispring.annotation.aop.Walkable;
import com.example.laispring.annotation.ioc.Component;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 20:58
 * @Description: TODO
 */
@Component
public class Pig implements Walkable {

    @Override
    public void run() {
        System.out.println("猪在跑");
    }
}

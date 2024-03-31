package com.example.laispring;

import com.example.laispring.annotation.aop.Walkable;
import com.example.laispring.config.LaiSpringConfig;
import com.example.laispring.ioc.LaiSpringApplicationContext;

/**
 * @Author: zurichscud
 * @Date: 2024/3/31 11:45
 * @Description: TODO
 */
public class Main {


    public static void main(String[] args) {
        LaiSpringApplicationContext ioc = new LaiSpringApplicationContext(LaiSpringConfig.class);
        Object pig = ioc.getBean("pig");
        ((Walkable) pig).run();

    }
}

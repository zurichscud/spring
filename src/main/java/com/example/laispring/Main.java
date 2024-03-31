package com.example.laispring;

import com.example.laispring.component.MyComponent;
import com.example.laispring.component.UserService;
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
        MyComponent bean = ioc.getBean("myComponent", MyComponent.class);
        System.out.println(bean);
        UserService bean2 = ioc.getBean("userService", UserService.class);
        System.out.println(bean2);
    }
}

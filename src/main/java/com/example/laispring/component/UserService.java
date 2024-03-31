package com.example.laispring.component;


import com.example.laispring.annotation.Autowired;
import com.example.laispring.annotation.Service;
import com.example.laispring.processor.InitializingBean;

/**
 * @Author: zurichscud
 * @Date: 2024/3/30 20:11
 * @Description: TODO
 */
@Service
public class UserService implements InitializingBean {
    @Autowired
    private UserDao userDao;

    //初始化方法
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("UserService的初始化方法");
    }
}

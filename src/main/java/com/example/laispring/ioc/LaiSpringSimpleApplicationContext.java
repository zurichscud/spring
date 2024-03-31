package com.example.laispring.ioc;


import com.example.laispring.annotation.ioc.*;
import com.example.laispring.config.LaiSpringConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zurichscud
 * @Date: 2024/3/30 19:09
 * @Description: IOC容器
 */
public class LaiSpringSimpleApplicationContext {
    private final Class<LaiSpringConfig> configClass;
    private ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();

    public LaiSpringSimpleApplicationContext(Class<LaiSpringConfig> configClass) {
        this.configClass = configClass;
        //获得ConfigClass的@ComponentScan
        ComponentScan componentScan = (ComponentScan) this.configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = componentScan.value();
        //System.out.println(value);
        String replace = value.replace(".", "/");
        //获取classpath
        URL resource = LaiSpringSimpleApplicationContext.class.getClassLoader().getResource(replace);
        //System.out.println(resource);
        File file = new File(resource.getPath());
        File[] files = file.listFiles();
        for (File temp : files) {
            //System.out.println(temp);
            String absolutePath = temp.getAbsolutePath();
            String substring = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.indexOf(".class"));
            System.out.println(substring);//获取类名
            String fullName = value.replace("/", ".") + "." + substring;
            System.out.println(fullName);
            makeBean(substring,fullName);
        }


    }

    private void makeBean(String className,String fullName) {
        try {
            Class<?> clazz = Class.forName(fullName);
            if (clazz.isAnnotationPresent(Controller.class) ||
                    clazz.isAnnotationPresent(Component.class) ||
                    clazz.isAnnotationPresent(Service.class) ||
                    clazz.isAnnotationPresent(Repository.class)) {
                Object o = clazz.newInstance();
                ioc.put(StringUtils.uncapitalize(className),o);
                display(ioc);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void display(ConcurrentHashMap<String, Object> ioc) {
        System.out.println("------------IOC list-------------");
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            System.out.println(entry.getKey()+"="+entry.getValue());

        }
    }
    public <T> T getBean(String beanName,Class<T> clazz){
        Object o = ioc.get(beanName);//类型检查
        if (o == null) {
            return null;
        }
        else {
            return (T)o;
        }

    }


}

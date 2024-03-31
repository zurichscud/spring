package com.example.laispring.ioc;


import com.example.laispring.annotation.*;
import com.example.laispring.config.LaiSpringConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zurichscud
 * @Date: 2024/3/30 19:09
 * @Description: 带有BeanDefinition的IOC容器
 */
public class LaiSpringApplicationContext2 {
    public static final String SINGLETON = "singleton";
    public static final String PROTOTYPE = "prototype";
    private final Class<LaiSpringConfig> configClass;
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singtonObjects = new ConcurrentHashMap<>();
    private URL resource;
    private String componentScanValue;

    public LaiSpringApplicationContext2(Class<LaiSpringConfig> configClass) {
        this.configClass = configClass;
        getComponentScanURL();
        getBeanDefinitionMap();
        display();

    }

    private void getBeanDefinitionMap() {
        File file = new File(resource.getPath());
        File[] files = file.listFiles();
        for (File temp : files) {
            //System.out.println(temp);
            String absolutePath = temp.getAbsolutePath();
            String className = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.indexOf(".class"));
            System.out.println(className);//获取类名
            String fullName = componentScanValue.replace("/", ".") + "." + className;
            //System.out.println(fullName);
            try {
                Class<?> clazz = Class.forName(fullName);
                if (clazz.isAnnotationPresent(Controller.class) ||
                        clazz.isAnnotationPresent(Component.class) ||
                        clazz.isAnnotationPresent(Service.class) ||
                        clazz.isAnnotationPresent(Repository.class)) {
                    if (clazz.isAnnotationPresent(Scope.class)){
                        String scope = clazz.getDeclaredAnnotation(Scope.class).value();
                        if (PROTOTYPE.equals(scope)) {
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setScope(scope);
                            beanDefinition.setClazz(clazz);
                            beanDefinitionMap.put(StringUtils.uncapitalize(className), beanDefinition);
                        }
                        else new RuntimeException("类型错误");
                    }
                    else {
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setScope("singleton");
                        beanDefinition.setClazz(clazz);
                        beanDefinitionMap.put(StringUtils.uncapitalize(className),beanDefinition);
                        //直接在单例池创建
                        initSingletonObjects();
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void getComponentScanURL() {
        ComponentScan componentScan = (ComponentScan) this.configClass.getDeclaredAnnotation(ComponentScan.class);
        componentScanValue = componentScan.value();
        //System.out.println(componentScanValue);
        String replace = componentScanValue.replace(".", "/");
        //获取classpath
        resource = LaiSpringApplicationContext2.class.getClassLoader().getResource(replace);
    }

    private void initSingletonObjects() {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (SINGLETON.equals(entry.getValue().getScope())){
                try {
                    Object object = entry.getValue().getClazz().newInstance();
                    singtonObjects.put(entry.getKey(),object);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }

    private void display() {
        System.out.println("------------IOC list-------------");
        for (Map.Entry<String, Object> entry : singtonObjects.entrySet()) {
            System.out.println(entry.getKey()+"="+entry.getValue());

        }
    }
    /**
     * @Description: 如果是单例直接从单例池返回bean，如果不是，则直接创建
     * @Param:
     * @Return:
     **/
    public <T> T getBean(String beanName,Class<T> clazz){
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (beanName.equals(entry.getKey())){
                if (SINGLETON.equals(entry.getValue().getScope())){
                    return ((T) singtonObjects.get(beanName));
                }
                else {
                    try {
                        Object object = entry.getValue().getClazz().newInstance();
                        return ((T) object);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        return null;
    }


}

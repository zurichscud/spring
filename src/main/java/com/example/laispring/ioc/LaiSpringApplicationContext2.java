package com.example.laispring.ioc;


import com.example.laispring.annotation.*;
import com.example.laispring.config.LaiSpringConfig;
import com.example.laispring.processor.BeanPostProcessor;
import com.example.laispring.processor.InitializingBean;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
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
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singtonObjects = new ConcurrentHashMap<>();
    private URL resource;
    private String componentScanValue;
    private ArrayList<BeanPostProcessor> processors=new ArrayList<>();

    public LaiSpringApplicationContext2(Class<LaiSpringConfig> configClass) {
        this.configClass = configClass;
        getComponentScanURL();
        getBeanDefinitionMap();
        initSingletonObjects();


        display();

    }
    /**
     * @Description: 将单例池中的processor放入list便于操作
     * @Param:
     * @Return:
     **/


    /**
     * @Description: 通过名称自动注入，依赖项必须先创建，否则会注入失败
     * @Param:
     * @Return:
     **/
    private void autowired(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object o = getBean(field.getName(), field.getType());
                try {
                    field.setAccessible(true);
                    field.set(object, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * @Description: 扫描指定的URI下的Class，并生成BeanDefinitionMap
     * 如果是Processor则直接创建（在Spring中Processor也创建在单例池，这里是为了简化）
     * @Param:
     * @Return:
     **/
    private void getBeanDefinitionMap() {
        File file = new File(resource.getPath());
        File[] files = file.listFiles();
        for (File temp : files) {
            //System.out.println(temp);
            String absolutePath = temp.getAbsolutePath();
            String className = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.indexOf(".class"));
            //System.out.println(className);//获取类名
            String fullName = componentScanValue.replace("/", ".") + "." + className;
            //System.out.println(fullName);
            try {
                Class<?> clazz = Class.forName(fullName);
                if (clazz.isAnnotationPresent(Controller.class) ||
                        clazz.isAnnotationPresent(Component.class) ||
                        clazz.isAnnotationPresent(Service.class) ||
                        clazz.isAnnotationPresent(Repository.class)) {
                    if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                        //如果是处理器则直接创建，跳出循环
                        createProcessor(clazz);
                        continue;
                    }
                    if (clazz.isAnnotationPresent(Scope.class)) {

                        String scope = clazz.getDeclaredAnnotation(Scope.class).value();
                        if (PROTOTYPE.equals(scope)) {
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setScope(scope);
                            beanDefinition.setClazz(clazz);
                            beanDefinitionMap.put(StringUtils.uncapitalize(className), beanDefinition);
                        } else new RuntimeException("类型错误");
                    } else {
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setScope("singleton");
                        beanDefinition.setClazz(clazz);
                        beanDefinitionMap.put(StringUtils.uncapitalize(className), beanDefinition);
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void createProcessor(Class<?> clazz) {
        try {
            Object object = clazz.newInstance();
            BeanPostProcessor beanPostProcessor = (BeanPostProcessor) object;
            processors.add(beanPostProcessor);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
            if (SINGLETON.equals(entry.getValue().getScope())) {
                singtonObjects.put(entry.getKey(), createBean(entry.getKey()));
            }
        }


    }

    private void useInitializingBean(Object object) {
        if (object instanceof InitializingBean) {
            try {
                ((InitializingBean) object).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void display() {
        System.out.println("------------IOC list-------------");
        for (Map.Entry<String, Object> entry : singtonObjects.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());

        }
    }

    /**
     * @Description: 如果是单例直接从单例池返回bean，如果不是，则直接创建
     * @Param:
     * @Return:
     **/
    public <T> T getBean(String beanName, Class<T> clazz) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (beanName.equals(entry.getKey())) {
                if (SINGLETON.equals(entry.getValue().getScope())) {
                    return ((T) singtonObjects.get(beanName));
                } else {
                    Object object = createBean(entry.getKey());
                    return ((T) object);
                }

            }
        }
        return null;
    }

    /**
     * @Description: 根据beanNaeme创建bean
     * @Param:
     * @Return:
     **/
    private Object createBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Object object = null;
        try {
            object = beanDefinition.getClazz().newInstance();
            autowired(object);
            /*before init*/
            for (BeanPostProcessor processor : processors) {
                object = processor.postProcessBeforeInitialization(object, beanName);
            }
            /*before init*/
            useInitializingBean(object);
            /*after init*/
            for (BeanPostProcessor processor : processors) {
                object = processor.postProcessAfterInitialization(object, beanName);
            }
            /*after init*/

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return object;


    }


}

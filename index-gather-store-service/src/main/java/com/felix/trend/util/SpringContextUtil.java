package com.felix.trend.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private SpringContextUtil() {
        System.out.println("SpringContextUtil()");
    }

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext){
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

}

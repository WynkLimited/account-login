package com.wynk.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WynkContextHolder implements ApplicationContextAware {

    private ApplicationContext appContext;

    @SuppressWarnings("serial")
    @Override
    public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
        if(null != appContext) {
            throw new BeansException("appContext is already set.") {
            };
        }
        appContext = appCtx;
    }

    public <T> Map<String, T> getBeansByType(Class<T> type) {
        return appContext.getBeansOfType(type);
    }

    public <B> B getBean(Class<B> clazz) {
        return appContext.getBean(clazz);
    }
    
    public <B> B getBeanByNameOfClass(String name, Class<B> clazz) {
        return clazz.cast(appContext.getBean(name, clazz));
    }

}

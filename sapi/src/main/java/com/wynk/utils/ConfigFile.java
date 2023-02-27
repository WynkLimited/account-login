package com.wynk.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:46 PM To change this
 * template use File | Settings | File Templates.
 */
public class ConfigFile extends PropertyPlaceholderConfigurer {

    Properties mergedProeprties;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            mergedProeprties = mergeProperties();
            convertProperties(mergedProeprties);
            processProperties(beanFactory, mergedProeprties);
            System.out.println("mergedProeprties = " + mergedProeprties);
        }
        catch (Exception ex) {
            throw new BeanInitializationException("Could not load properties", ex);
        }
    }

    public Properties getMergedProeprties() {
        return mergedProeprties;
    }

    /**
     * *********************************************************************************************
     * ** *************************************** Utility Methods
     * **************************************
     * *******************************************************
     * ****************************************
     */
    public int getIntProperty(String propName, int defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public long getLongProperty(String propName, long defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Long.parseLong(value);
    }

    public boolean getBooleanProperty(String propName, boolean defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public String getStringProperty(String propName, String defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(org.apache.commons.lang3.StringUtils.isEmpty(value) ) {
            return defaultValue;
        }
        return value;
    }

    public double getDoubleProperty(String propName, double defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    public List<String> getListProperty(String propName, String defaultValue) {
        String value = mergedProeprties.getProperty(propName);
        if(value == null || value.isEmpty()) {
            if((defaultValue == null) || defaultValue.isEmpty()) {
                return new ArrayList<String>();
            }
            else {
                value = defaultValue;
            }
        }
        return Arrays.asList(value.split(","));

    }
}

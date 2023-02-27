package com.wynk.service.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.springframework.beans.factory.annotation.Value;

public class WCFCommandSetterFactory {
    public static WCFCommandSetterFactory INSTANCE = new WCFCommandSetterFactory();

    public HystrixCommand.Setter getSetter(String commandGroupKey, int timeout, int threadPoolSize, int threadPoolQueue) {
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout);
        HystrixThreadPoolProperties.Setter threadPoolSetter = HystrixThreadPoolProperties.Setter().withCoreSize(threadPoolSize).withMaxQueueSize(threadPoolQueue);
        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey)).andCommandPropertiesDefaults(commandProperties).andThreadPoolPropertiesDefaults(threadPoolSetter);
    }
}

package com.wynk.service.hystrix;


import com.netflix.hystrix.HystrixCommand;

import java.util.HashMap;

public class HystrixUtils {
    public static String WCF_COMMAND_GROUP_KEY = "WCF-Commands";
    public static String HYSTRIX_FALLBACK_LOGS = "HYSTRIX_FALLBACK_LOGS";

    public static HashMap<String, String> getCommandProperties(HystrixCommand command) {
        HashMap<String, String> commandMeta = new HashMap<>();
        commandMeta.put("key", command.getCommandKey().toString());
        commandMeta.put("exception", command.getExecutionException() != null ? command.getExecutionException().getMessage() : "");
        commandMeta.put("exceptionMsg", command.getFailedExecutionException() != null ? command.getFailedExecutionException().toString() : "");
        commandMeta.put("time", String.valueOf(command.getExecutionTimeInMilliseconds()));
        commandMeta.put("circuitBroken", String.valueOf(command.isCircuitBreakerOpen()));
        return commandMeta;
    }
}


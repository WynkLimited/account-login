package com.wynk.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/05/14
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseService {
    private static ObjectMapper objectMapper = null;
    private static MessageSource messageSource;

    protected ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (BaseService.class) {
                if (objectMapper == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper = mapper;
                }
            }
        }
        return objectMapper;
    }

    public static MessageSource getMessageSource() {
        return messageSource;
    }

    @Autowired
    private void setMessageSource(MessageSource messageSource) {
        BaseService.messageSource = messageSource;
    }
}

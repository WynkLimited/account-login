package com.wynk.utils;

import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ankit Srivastava on 08/02/19.
 */
@Component
public class ABUtils {

    private final Logger logger = LoggerFactory.getLogger(ABUtils.class.getCanonicalName());

    public static final String X_BSY_AB 	= "x-bsy-ab";

    public static Map<String, String> getABHeader(HttpRequest request) {
        if(request.headers() != null && request.headers().get(X_BSY_AB)!=null){
            return Arrays.stream(request.headers().get(X_BSY_AB).split("\\|"))
                    .filter(e -> e.contains(":"))
                    .collect(Collectors.toMap(s -> s.split(":")[0], s -> s.split(":")[1]));
        }
        else{
            return null;
        }
    }
}

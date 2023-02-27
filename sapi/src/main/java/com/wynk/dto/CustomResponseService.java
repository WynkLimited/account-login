package com.wynk.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wynk.exceptions.MusicErrorType;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kunal Sharma
 * @since : 05/10/22, Wednesday
 **/
public class CustomResponseService {

    private static final Logger logger = LoggerFactory.getLogger(CustomResponseService.class);
    private static CharSequence DEFAULT_ERROR_RESPONSE = StringUtils.EMPTY;

    private static final String JSON_CONTENT = "application/json; charset=UTF-8";

    private static final ObjectMapper MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


    static {
        try {
            DEFAULT_ERROR_RESPONSE = MAPPER.writeValueAsString(
                    new ExceptionDetails(MusicErrorType.MUS999.getErrorCode(), MusicErrorType.MUS999.getErrorTitle(), MusicErrorType.MUS999.getErrorMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR));
        } catch (JsonProcessingException e) {
            logger.error("This is too much!!! I Quit");
        }
    }


    public static DefaultFullHttpResponse getGenericFailureResponse() {
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer(DEFAULT_ERROR_RESPONSE, CharsetUtil.UTF_8));
        httpResponse.headers().set(CONTENT_TYPE, JSON_CONTENT).set(CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }


    private static class ExceptionDetails {

        @JsonProperty("errorcode")
        private String errorCode;

        @JsonProperty("errortitle")
        private String errorTitle;

        @JsonProperty("error")
        private String error;

        @JsonIgnore
        private transient HttpResponseStatus httpResponseStatus;

        public ExceptionDetails() {

        }

        public ExceptionDetails(String errorCode, String errorTitle, String error, HttpResponseStatus httpResponseStatus) {
            super();
            this.errorCode = errorCode;
            this.errorTitle = errorTitle;
            this.error = error;
            this.httpResponseStatus = httpResponseStatus;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public void setErrorTitle(String errorTitle) {
            this.errorTitle = errorTitle;
        }

        public void setError(String error) {
            this.error = error;
        }

        public HttpResponseStatus getHttpResponseStatus() {
            return httpResponseStatus;
        }

        public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
            this.httpResponseStatus = httpResponseStatus;
        }

    }
}
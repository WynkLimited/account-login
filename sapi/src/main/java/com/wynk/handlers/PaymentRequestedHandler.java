package com.wynk.handlers;

import com.wynk.common.PortalException;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.service.PaymentService;
import com.wynk.service.UserAuthorizationService;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author : Kunal Sharma
 * @since : 29/09/22, Thursday
 **/

@Controller("/v3/user/payment.*")
public class PaymentRequestedHandler implements IUrlRequestHandler, IAuthenticatedUrlRequestHandler {

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    private Logger logger = LoggerFactory.getLogger(PaymentRequestedHandler.class);

    @Autowired
    private PaymentService paymentService;


    @Override
    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        return userAuthorizationService.authenticate(request, requestUri, requestPayload);
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException, OTPAuthorizationException {
        if (logger.isInfoEnabled() && !requestUri.contains("headers")) {
            logger.info("Received request " + requestUri + " with payload " + requestPayload);
            logger.info("payment full request is {}", request);
        }

        if(request.getMethod() == HttpMethod.POST) {
              if (requestUri.equalsIgnoreCase("/v3/user/payment")) {
                return paymentService.initializePayRequest(requestPayload, request);
            }

        }

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }
}

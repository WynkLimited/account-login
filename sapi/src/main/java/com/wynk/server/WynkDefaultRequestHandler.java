package com.wynk.server;


import com.wynk.constants.Constants;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.handlers.IAuthenticatedUrlRequestHandler;
import com.wynk.handlers.IUrlRequestHandler;
import com.wynk.utils.*;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by anurag on 12/7/16.
 */
@ChannelHandler.Sharable
public class WynkDefaultRequestHandler extends  BaseHttpRequestHandler implements ApplicationContextAware {

	private static Logger logger             = LoggerFactory.getLogger(WynkDefaultRequestHandler.class);

	private final EventExecutorGroup 		executor;

	private ApplicationContext              applicationContext;

	private Map<String, IUrlRequestHandler> handlerClassMap = new ConcurrentHashMap<String, IUrlRequestHandler>();

	public WynkDefaultRequestHandler(EventExecutorGroup executor) {
		this.executor = executor;
	}

	private static final String DEFAULT_CORS = "https://wynk.in";

	@Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	protected FullHttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) {

		if(logger.isDebugEnabled()) {
			logger.debug("Request URI : " + requestUri + " , message " + requestPayload);
		}

		long startTime = System.currentTimeMillis();
		FullHttpResponse response = null;
		int i = requestUri.indexOf("?");
		String requestUriWoutParams = requestUri;
		if(i > 0) {
			requestUriWoutParams = requestUri.substring(0, i);
		}

		if(requestUri.contains("loaderio-b79949f677c22cb5f739269561597c09"))
			return HttpResponseService.createOKResponse("loaderio-b79949f677c22cb5f739269561597c09");

		long st1 = System.currentTimeMillis();
		IUrlRequestHandler urlRequestHandler = lookupUrlHandler(requestUriWoutParams);
		if(urlRequestHandler == null) {
			return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
		}

		try {
			if(requestUri.contains("headers"))
			{
				//request.headers().add("x-bsy-ip", getRemoteAddress().getAddress().getHostAddress());
				logger.info("Setting up context with value COO {}", UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COO));
				ChannelContext.setUserCooContext(UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COO));
				logger.info("Setting up context with value COA {}", UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COA));
				ChannelContext.setUserCoaContext(UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COA));
				response = (FullHttpResponse) urlRequestHandler.handleRequest(requestUri, requestPayload, request);
				if(response == null) {
					return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
				}
				return response;
			}

			//request.headers().add("x-bsy-ip",getRemoteAddress().getAddress().getHostAddress());

			ChannelContext.setRequest(request);

			Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
			if(urlParameters != null)
			{
				String lang = HTTPUtils.getStringParameter(urlParameters, "lang");
				if(!StringUtils.isEmpty(lang))
					ChannelContext.setLang(lang);
			}

			boolean forceUpdateNDSInfo = ObjectUtils.getBoolean(Utils.getURLParam(urlParameters, "fuNDS"), false);
			ChannelContext.getRequestContext().setForceUpdateNDSInfo(forceUpdateNDSInfo);

			logger.info("Setting up context with COO {}", UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COO));
			ChannelContext.setUserCooContext(UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COO));
			logger.info("Setting up context with COA {}", UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COA));
			ChannelContext.setUserCoaContext(UserUtils.getHeaderValue(request.headers(), Constants.RequestHeaders.X_BSY_COA));

			if(urlRequestHandler instanceof IAuthenticatedUrlRequestHandler)
			{
				boolean authenticated =  ((IAuthenticatedUrlRequestHandler)urlRequestHandler).authenticate(requestUri, requestPayload, request);
				if(!authenticated){
					return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
				}

			}

			response =  (FullHttpResponse) urlRequestHandler.handleRequest(requestUri, requestPayload, request);
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);
			logger.info("Response for URI : {}, Response : {}, ResponseTime : {}", requestUri, response.getStatus(), duration);
			if(requestUri.contains("music/") || requestUri.contains("health/check"))
			{
				response.headers().add("Access-Control-Allow-Origin", getCorsValue(request));
				response.headers().add("Access-Control-Expose-Headers", "a,w,k,q,p,n,m,z,y,c");
				response.headers().add("Access-Control-Max-Age", 1728000);
				response.headers().add("Access-Control-Allow-Methods", "OPTIONS, HEAD, GET, POST, PUT, DELETE");
				response.headers().add("Access-Control-Allow-Headers", "X-Requested-With, x-msisdn, x-bsy-msisdn,x-bsy-utkn, x-bsy-wap, x-bsy-iswap, x-bsy-did,x-bsy-wynk, x-bsy-medium,x-bsy-cip,x-bsy-das,x-bsy-ptot, Content-Type, Content-Length,sk,tk,bk,pk,x-bsy-cip,x-bsy-das,x-bsy-ptot,x-bsy-cid,sk,tk,bk,pk,authority,cache-control,pragma,x-bsy-coo,x-bsy-coa");
			}
			LogstashLoggerUtils
					.createAccessLog(response, requestUriWoutParams, requestUri, requestPayload, duration, request, "");
			return response;
		}
		catch (OTPAuthorizationException e) {
			return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
		}
//		catch(Exception e) {
//			//provide string in marker
//			ByteBuf content = PooledByteBufAllocator.DEFAULT.buffer(0, e.getMessage().getBytes().length);
//			content.writeBytes(e.getMessage().getBytes());
//			return new DefaultExceptionHandler(HttpVersion.HTTP_1_1, e.status,content);
//		}
		catch (Exception e) {
			logger.error("Error occurred while handling request : uri [{}] payload [{}] Error [{}]", requestUri, requestPayload, ExceptionUtils.getStackTrace(e));
			LogstashLoggerUtils
					.createAccessLog(response, requestUriWoutParams, requestUri, requestPayload, System.currentTimeMillis() - startTime, request,
							ExceptionUtils.getStackTrace(e));
			return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public IUrlRequestHandler lookupUrlHandler(String url) {
		IUrlRequestHandler handler = handlerClassMap.get(url);
		if(handler != null) {
			return handler;
		}
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		for(String beanName : beanDefinitionNames) {
			Object obj = applicationContext.getBean(beanName);
			if(obj == null) {
				logger.info(beanName + " is not initialized by spring");
				continue;// spring is mad giving null all over places
			}
			Controller controller = obj.getClass().getAnnotation(Controller.class);
			if(controller != null) {
				if(url.matches(controller.value())) {
					handler = (IUrlRequestHandler) obj;
					handlerClassMap.put(url, handler);
					return handler;
				}
			}
		}

		return null;
	}

	private String getCorsValue(HttpRequest requestContext) {
		try {
			logger.info("get cors value for request : " + requestContext.uri() );
			HttpHeaders headers = requestContext.headers();
			if (headers == null || headers.get("Origin") == null) {
				return DEFAULT_CORS;
			}
			String origin = headers.get("Origin");
			if (origin.endsWith("wynk.in") || origin.endsWith("airtelxstream.in")) {
				return origin;
			} else {
				return DEFAULT_CORS;
			}
		} catch (Exception e) {
			logger.error(
					"Error while fetching cors error , will sending default  now  : "
							+ DEFAULT_CORS
							+ "  Request is :  "
							+ requestContext.uri(),
					e);
			return DEFAULT_CORS;
		}
	}

}

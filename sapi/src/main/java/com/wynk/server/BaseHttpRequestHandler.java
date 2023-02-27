package com.wynk.server;

import com.wynk.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by anurag on 12/8/16.
 */
@ChannelHandler.Sharable
public abstract class BaseHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger logger = LoggerFactory.getLogger(BaseHttpRequestHandler.class.getCanonicalName());

	@Override protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final FullHttpRequest request)
			throws Exception {
		InetSocketAddress remoteAddress = null;
		String clientIP = null;
		try {
			MDC.clear();
			MDC.put("rid", Utils.getRequestId());
			remoteAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
			clientIP = remoteAddress.getAddress().getHostAddress();
			//interceptors.add(new ChannelInterceptor());

			QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri(), CharsetUtil.UTF_8);
			String requestUri = queryDecoder.path();

			ByteBuf content = request.content();
			String requestPayload = org.apache.commons.lang3.StringUtils.EMPTY;
			if(content.isReadable()) {
				requestPayload = content.toString(CharsetUtil.UTF_8);
			}

			if(!StringUtils.isBlank(requestUri)) {
				String headers = "";
				if (null != request) {
					if (null != request.headers() && null != request.headers().entries()) {
						headers = request.headers().entries().toString();
					}
					logger.info("Request URI accessed: " + request.getUri() + " headers: " + headers);
				} else {
					logger.info("Some strange empty request");
				}
			}

			interceptOnRequestReceived(channelHandlerContext,request);
			request.headers().add("x-bsy-ip", remoteAddress.getAddress().getHostAddress());

			FullHttpResponse response = handleRequest(request.uri(), requestPayload, request);
			interceptOnRequestSucceed(channelHandlerContext, response);
			writeResponse(channelHandlerContext, response, request);
			MDC.remove("rid");
		}
		catch (Exception ex) {
			logger.error("clientIP:" + clientIP + ":MessageEvent remoteAddress :" + remoteAddress);
			interceptOnRequestFailed(channelHandlerContext, ex);
			throw ex;
		}
	}


	protected void writeResponse(final ChannelHandlerContext ctx, FullHttpResponse response, FullHttpRequest httpRequest) {
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpHeaders.isKeepAlive(httpRequest);
		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		// Encode the cookie.
		/*try{
			String cookieString = httpRequest.headers().get(HttpHeaders.Names.COOKIE);
			if (cookieString != null) {
				Set<Cookie> cookies = CookieDecoder.decode(cookieString);
				if (!cookies.isEmpty()) {
					// Reset the cookies if necessary.
					for (Cookie cookie : cookies) {
						response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
					}
				}
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			logger.error("Error setting cookie : "+ e1.getMessage(), e1);
		}*/

		// Write the response.
		ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);;

//		if(!keepAlive)
//			future.addListener(ChannelFutureListener.CLOSE);
	}


	private void interceptOnRequestReceived(ChannelHandlerContext ctx, HttpRequest request) {

		ChannelThreadLocal.set(ctx.channel());
	}

	private void interceptOnRequestSucceed(ChannelHandlerContext ctx,
			HttpResponse response) {
		ChannelThreadLocal.unset();
	}

	private void interceptOnRequestFailed(ChannelHandlerContext ctx,
			Throwable e) {
		ChannelThreadLocal.unset();
	}

	/**
	 * Handle HTTP request
	 * @param requestUri
	 * @param requestPayload
	 * @param request
	 * @return
	 */
	protected abstract FullHttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("ChannelError : " + cause.getMessage());
		logger.error(Utils.getStackTrace(cause));

		interceptOnRequestFailed(ctx, cause);
		ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

}

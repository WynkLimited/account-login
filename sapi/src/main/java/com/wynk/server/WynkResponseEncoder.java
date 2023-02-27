package com.wynk.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static io.netty.util.CharsetUtil.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Created by anurag on 12/19/16.
 */
@Component
@ChannelHandler.Sharable
public class WynkResponseEncoder extends ChannelOutboundHandlerAdapter {

	private static Logger LOG = LoggerFactory.getLogger(WynkResponseEncoder.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		FullHttpResponse response = null;
		try {
			if(!(msg instanceof HttpResponse)) {
				response =  HttpResponseService.createOKResponse(msg.toString());
			}
			else {
				response = (FullHttpResponse) msg;
			}
		}
		catch (Throwable th) {
			LOG.error("APPLICATION_ERROR", "Error while creating response for obj: {}, ERROR: {}", new Object[]{ msg, th.getMessage(), th });
			response = HttpResponseService.getGenericFailureResponse();
		}
		finally {
			ctx.writeAndFlush(response, promise).addListener(ChannelFutureListener.CLOSE);;
//			ctx.close();
		}
	}

}

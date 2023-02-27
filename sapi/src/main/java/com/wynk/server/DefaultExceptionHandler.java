package com.wynk.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by anurag on 12/19/16.
 */
@Component
@ChannelHandler.Sharable
public class DefaultExceptionHandler extends ChannelInboundHandlerAdapter {

	private final Logger LOG = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("Error while processing your request, ERROR: {}", cause.getMessage(), cause);
		ctx.write(cause);
	}
}

package com.wynk.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by anurag on 12/7/16.
 */
@Component
public class HttpServerChannelPipeline extends ChannelInitializer<Channel> {

	@Autowired
	private EventExecutorGroup executorGroup;

	@Autowired
	private WynkDefaultRequestHandler wynkDefaultRequestHandler;

	@Autowired
	private DefaultExceptionHandler exceptionHandler;

	@Autowired
	private WynkResponseEncoder responseEncoder;

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("httpcodec", new HttpServerCodec());
		pipeline.addLast("inflater", new HttpContentDecompressor());
		pipeline.addLast("httpAggregator", new HttpObjectAggregator(1048576));
		pipeline.addLast("deflater", new HttpContentCompressor());

		pipeline.addLast("httpResponseEncoder", responseEncoder);
		pipeline.addLast(executorGroup, "defaultHandler", wynkDefaultRequestHandler);
		pipeline.addLast("httpExceptionHandler", exceptionHandler);
	}

}

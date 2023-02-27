package com.wynk.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by bhuvangupta on 08/05/14.
 */
public interface NettyInterceptor {

    void onRequestReceived(ChannelHandlerContext ctx, HttpRequest request);

    void onRequestCompleted(ChannelHandlerContext ctx, HttpResponse response);

    void onRequestFailed(ChannelHandlerContext ctx, Throwable cause);
}


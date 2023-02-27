package com.wynk.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by bhuvangupta on 08/05/14.
 */
public class ChannelInterceptor implements NettyInterceptor {

    @Override
    public void onRequestFailed(ChannelHandlerContext ctx, Throwable e) {
        ChannelThreadLocal.unset();
    }

    @Override
    public void onRequestReceived(ChannelHandlerContext ctx, HttpRequest request) {
        ChannelThreadLocal.set(ctx.channel());
    }

    @Override
    public void onRequestCompleted(ChannelHandlerContext ctx,
                                   HttpResponse response) {
        ChannelThreadLocal.unset();
    }



}
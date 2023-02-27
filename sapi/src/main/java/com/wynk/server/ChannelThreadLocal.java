package com.wynk.server;

import io.netty.channel.Channel;

/**
 * Created by bhuvangupta on 08/05/14.
 */
public class ChannelThreadLocal {

    public static final ThreadLocal<Channel> CHANNEL_THREAD_LOCAL
            = new ThreadLocal<Channel>();

    private ChannelThreadLocal() {
    }

    public static void set(Channel channel) {
        CHANNEL_THREAD_LOCAL.set(channel);
    }

    public static void unset() {
        ChannelContext.unset();
        CHANNEL_THREAD_LOCAL.remove();
    }

    public static Channel get() {
        return CHANNEL_THREAD_LOCAL.get();
    }
}

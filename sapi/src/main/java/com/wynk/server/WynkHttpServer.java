package com.wynk.server;

import com.wynk.config.NettyConfig;
import com.wynk.utils.Utils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Created by anurag on 12/7/16.
 */
public class WynkHttpServer {

	private final static Logger logger = LoggerFactory.getLogger(WynkHttpServer.class);

	private ApplicationContext 		appContext;
	private ChannelGroup 			channelGroup;
	private EventLoopGroup 			workerGroup;
	private EventLoopGroup      	bossGroup;

	private void initSpringContext() {
		String[] locations = { "beans.xml" };
		appContext = new ClassPathXmlApplicationContext(locations);
		if(appContext instanceof AbstractApplicationContext) {
			AbstractApplicationContext abstractAppContext = (AbstractApplicationContext) appContext;
			abstractAppContext.registerShutdownHook();
		}
	}


	private void run() throws Throwable {
		try {
			initSpringContext();

			NettyConfig serverConfig = appContext.getBean(NettyConfig.class);

			// Configure the server - based on OS.
			String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
			Class<? extends ServerChannel> clazz = null;

			//native socket transport for Linux - for better Performance
			if(name.startsWith("linux")) {
				bossGroup = new EpollEventLoopGroup(serverConfig.getBossThreads());
				workerGroup = new EpollEventLoopGroup(serverConfig.getNumThreads());
				clazz = EpollServerSocketChannel.class;
			}
			else {
				bossGroup = new NioEventLoopGroup(serverConfig.getBossThreads());
				workerGroup = new NioEventLoopGroup(serverConfig.getNumThreads());
				clazz = NioServerSocketChannel.class;
			}


			ServerBootstrap bootstrap = new ServerBootstrap();
			ChannelFuture future = bootstrap.group(bossGroup, workerGroup).channel(clazz).childHandler(appContext.getBean(HttpServerChannelPipeline.class)).localAddress(serverConfig.getHttpport())
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).bind().syncUninterruptibly();
			String infoString = "Wynk API Server started on port " + serverConfig.getHttpport() + " @ " + new Date(
					System.currentTimeMillis());
			System.out.println(infoString);
			logger.info(infoString);

			channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
			channelGroup.add(future.channel());

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					shutdown();
				}
			});

			future.channel().closeFuture().sync();

		}
		catch (Throwable th) {
			String errorMessage = "Server could not be started, ERROR: " + th.getMessage();
			logger.error("APPLICATION_ERROR", th.getMessage(), th);
			System.err.println(errorMessage);
			throw th;
		}
		finally {

		}
	}


	private void shutdown() {
		logger.info("[wynk-server] Shutting down ...");
		if(null != workerGroup) {
			Future<?> shutdownFuture = workerGroup.shutdownGracefully();
			shutdownFuture.syncUninterruptibly();
		}
		if(null != channelGroup) {
			ChannelGroupFuture future = channelGroup.close();
			future.syncUninterruptibly();
		}
		logger.info("End of pipeline executor");
		logger.info("[wynk-server] Shutdown successful.");
	}

	public static void main(String[] args) {
		WynkHttpServer wynkHttpServer = new WynkHttpServer();
		try {
			wynkHttpServer.run();
		}
		catch (Throwable e) {
			System.out.println(Utils.getErrorMessage(e));
			e.printStackTrace();
			logger.error(Utils.getErrorMessage(e), e);
		}
	}

}

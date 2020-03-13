package cn.com.yusys.icsp.agent.server.socket;

import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettySocketFileService {
	private int port;
	private int connThreadNum;
	private int workThreadNum;
	EventLoopGroup pGroup;
	EventLoopGroup cGroup;

	public NettySocketFileService() {
		this.connThreadNum = 20;
		this.workThreadNum = 10;
	}

	public NettySocketFileService(final int port, final int connThreadNum, final int workThreadNum) {
		this.connThreadNum = 20;
		this.workThreadNum = 10;
		this.port = port;
		this.connThreadNum = connThreadNum;
		this.workThreadNum = workThreadNum;
	}

	public void start() {
		// 初始化用于Acceptor的主"线程池"以及用于I/O工作的从"线程池"；
		this.pGroup = new NioEventLoopGroup(this.connThreadNum);
		this.cGroup = new NioEventLoopGroup(this.workThreadNum);
		ServerBootstrap b = new ServerBootstrap(); // 初始化ServerBootstrap实例， 此实例是netty服务端应用开发的入口
		b.group(pGroup, cGroup)// 通过ServerBootstrap的group方法，设置（1）中初始化的主从"线程池"；
				.channel(NioServerSocketChannel.class) // 定通道channel的类型，由于是服务端，故而是NioServerSocketChannel；
				.childHandler(new ChannelInitializer<SocketChannel>() { // 设置子通道也就是SocketChannel的处理器， 其内部是实际业务开发的"主战场"
					@Override
					public void initChannel(SocketChannel socketChannel) throws Exception {
						socketChannel.pipeline().addLast(new ObjectEncoder());
						socketChannel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE,
								ClassResolvers.weakCachingConcurrentResolver(null)));
						socketChannel.pipeline().addLast(new FileSplitUploadServerHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 1048576);// BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50
		// .childOption(ChannelOption.SO_KEEPALIVE, true); //
		// (是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。

		// Bind and start to accept incoming connections.
		try {
			final ChannelFuture cf = b.bind(this.port).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			LoggerFactory.getLogger(NettySocketFileService.class).error(e.getMessage(), e);
		}
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void stop() {
		try {
			this.pGroup.shutdownGracefully();
			this.cGroup.shutdownGracefully();
		} catch (Exception e) {
			LoggerFactory.getLogger(NettySocketFileService.class).error(e.getMessage(), e);
		}
	}

	public void destroy() {
		this.stop();
	}

	public int getConnThreadNum() {
		return this.connThreadNum;
	}

	public void setConnThreadNum(final int connThreadNum) {
		this.connThreadNum = connThreadNum;
	}

	public int getWorkThreadNum() {
		return this.workThreadNum;
	}

	public void setWorkThreadNum(final int workThreadNum) {
		this.workThreadNum = workThreadNum;
	}
}

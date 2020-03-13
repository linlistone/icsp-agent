package cn.com.yusys.icsp.agent.server.start;

import java.util.Objects;

import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.socket.NettySocketFileService;

public class SocketServiceRegistry {
	private static NettySocketFileService socketService;

	public static void start() throws Exception {
		final Thread thread = new Thread(() -> (SocketServiceRegistry.socketService = new NettySocketFileService(
				StartParameter.parameter.getSocketPort(), 20, 10)).start());
		thread.setDaemon(true);
		thread.start();
	}

	public static void stop() throws Exception {
		if (Objects.nonNull(SocketServiceRegistry.socketService)) {
			SocketServiceRegistry.socketService.stop();
		}
	}

	static {
		SocketServiceRegistry.socketService = null;
	}
}

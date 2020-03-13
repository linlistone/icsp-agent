package cn.com.yusys.icsp.agent.server.start;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.AgentRegistryInfo;
import cn.com.yusys.icsp.agent.common.constants.AgentStatusEnum;
import cn.com.yusys.icsp.agent.common.util.AgentUtil;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.os.OSHandlerFactory;
import cn.com.yusys.icsp.agent.server.util.AgentServerUtil;
import cn.com.yusys.icsp.agent.server.util.OkhttpClient;

public final class AgentRegistry {
	private static final Logger logger = LoggerFactory.getLogger(AgentRegistry.class);
	private static final String REGISTRY_URI = "/api/agent/registry";
	private static Queue<String> failureRegistryAddressQueue = new ConcurrentLinkedQueue<String>();
	private static ExecutorService execService = Executors.newSingleThreadExecutor();

	public static void start() {
		final String addresses = StartParameter.parameter.getRegistryAddresses();
		if (addresses == null || "".equals(addresses)) {
			AgentRegistry.logger.warn("管控平台注册地址为空!");
		}
		final List<String> listAddress = Arrays.asList(addresses.split(","));
		for (final String address : listAddress) {
			agentRegistry(address.trim());
		}
		AgentRegistry.execService.execute(new RetryRegistryTask());
	}

	public static void agentRegistry(final String address) {
		AgentRegistryInfo info = new AgentRegistryInfo();
		info.setHostname(StartParameter.parameter.getHostname());
		info.setHostAddress(StartParameter.parameter.getHostAddress());
		info.setRmiPort(StartParameter.parameter.getRmiPort());
		info.setSocketPort(StartParameter.parameter.getSocketPort());
		info.setVersion("Agent_1.0.0");
		info.setRegistryTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		info.setRmiStatus(getRmiStatus());
		info.setSocketStatus(getSocketStatus());
		info.setStatus(AgentStatusEnum.UP.toString());
		info.setAppWorkspace(StartParameter.parameter.getFilePath());
		info.setOsName(StartParameter.parameter.getOsName());
		try {
			OkhttpClient.httpPost(AgentUtil.builder(70, "http://", address, REGISTRY_URI), info);
			if (!info.getSocketStatus().equals(AgentStatusEnum.UP.toString())
					|| !info.getRmiStatus().equals(AgentStatusEnum.UP.toString())) {
				AgentRegistry.failureRegistryAddressQueue.add(address);
			}
		} catch (Exception e) {
			AgentRegistry.logger.error(e.getMessage(), e);
		}
	}

	private static String getSocketStatus() {
		return getStatus(StartParameter.parameter.getSocketPort());
	}

	private static String getRmiStatus() {
		return getStatus(StartParameter.parameter.getRmiPort());
	}

	private static String getStatus(final int port) {
		final boolean isListen = AgentServerUtil.isListen(OSHandlerFactory.isWin(), port);
		return isListen ? AgentStatusEnum.UP.toString() : AgentStatusEnum.START.toString();
	}

	public static void stop() {
		synchronized (AgentRegistry.failureRegistryAddressQueue) {
			AgentRegistry.failureRegistryAddressQueue.clear();
			AgentRegistry.execService.shutdown();
		}
	}

	private static class RetryRegistryTask implements Runnable {
		@Override
		public void run() {
			while (!AgentRegistry.failureRegistryAddressQueue.isEmpty()) {
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					AgentRegistry.logger.error(e.getMessage(), e);
				}
				if (!AgentRegistry.failureRegistryAddressQueue.isEmpty()) {
					String address = AgentRegistry.failureRegistryAddressQueue.poll();
					AgentRegistry.agentRegistry(address);
				}
			}
			AgentRegistry.execService.shutdown();
		}
	}
}

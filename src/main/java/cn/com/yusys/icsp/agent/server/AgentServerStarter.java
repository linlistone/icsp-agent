package cn.com.yusys.icsp.agent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.start.AgentRegistry;
import cn.com.yusys.icsp.agent.server.start.JMXServiceRegistry;
import cn.com.yusys.icsp.agent.server.start.SocketServiceRegistry;
import cn.com.yusys.icsp.agent.server.util.AgentServerUtil;

public class AgentServerStarter {
	private static final Logger log = LoggerFactory.getLogger(AgentServerStarter.class);

	public static void main(final String[] args) throws Exception {
		final long start = System.currentTimeMillis();
		AgentServerUtil.parse(args);
		log.info("Agent-Server start parameters:{}", JSON.toJSONString(StartParameter.parameter));
		JMXServiceRegistry.start();
		log.info("JMXConnectorServer is started!!!");
		SocketServiceRegistry.start();
		log.info("SocketFileService is started!!!");
		AgentRegistry.start();
		log.info("Registry Service Control platfrom success!");
		log.info("Agent-Server version:{},start cost:{}", "Agent_1.0.0", System.currentTimeMillis() - start);
	}

}
